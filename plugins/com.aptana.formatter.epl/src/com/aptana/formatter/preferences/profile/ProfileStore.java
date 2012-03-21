/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *******************************************************************************/
package com.aptana.formatter.preferences.profile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.formatter.IDebugScopes;
import com.aptana.formatter.epl.FormatterPlugin;

/**
 * Can load/store profiles from/to profilesKey
 */
public class ProfileStore implements IProfileStore
{

	/** The default encoding to use */
	public static final String ENCODING = IOUtil.UTF_8;

	/**
	 * A SAX event handler to parse the xml format for profiles.
	 */
	private final class ProfileDefaultHandler extends DefaultHandler
	{

		private List<IProfile> fProfiles;
		private int fVersion;

		private String fName;
		private Map<String, String> fSettings;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{

			if (qName.equals(XML_NODE_SETTING))
			{

				final String key = attributes.getValue(XML_ATTRIBUTE_ID);
				final String value = attributes.getValue(XML_ATTRIBUTE_VALUE);
				fSettings.put(key, value);

			}
			else if (qName.equals(XML_NODE_PROFILE))
			{

				fName = attributes.getValue(XML_ATTRIBUTE_NAME);
				try
				{
					fVersion = Integer.parseInt(attributes.getValue(XML_ATTRIBUTE_VERSION));
				}
				catch (NumberFormatException ex)
				{
					throw new SAXException(ex);
				}

				fSettings = new HashMap<String, String>(200);

			}
			else if (qName.equals(XML_NODE_ROOT))
			{
				fProfiles = new ArrayList<IProfile>();
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		{
			if (qName.equals(XML_NODE_PROFILE))
			{
				for (Map.Entry<String, String> entry : defaults.entrySet())
				{
					if (!fSettings.containsKey(entry.getKey()))
					{
						fSettings.put(entry.getKey(), entry.getValue());
					}
				}
				fProfiles.add(new CustomProfile(fName, fSettings, fVersion));
				fName = null;
				fSettings = null;
				fVersion = 1;
			}
		}

		public List<IProfile> getProfiles()
		{
			return fProfiles;
		}

	}

	/**
	 * Identifiers for the XML file.
	 */
	private final static String XML_NODE_ROOT = "profiles"; //$NON-NLS-1$
	private final static String XML_NODE_PROFILE = "profile"; //$NON-NLS-1$
	private final static String XML_NODE_SETTING = "setting"; //$NON-NLS-1$

	private final static String XML_ATTRIBUTE_VERSION = "version"; //$NON-NLS-1$
	private final static String XML_ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	private final static String XML_ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	private final static String XML_ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$

	private final Map<String, String> defaults;

	public ProfileStore(IProfileVersioner versioner, Map<String, String> defaults)
	{
		this.versioner = versioner;
		this.defaults = defaults;
	}

	/**
	 * @return the profile versioner
	 */
	public IProfileVersioner getVersioner()
	{
		return versioner;
	}

	/**
	 * Returns an XML from the given profiles.
	 * 
	 * @param profiles
	 * @return An XML string
	 * @throws CoreException
	 */
	public String writeProfiles(Collection<IProfile> profiles) throws CoreException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream(2000);
		try
		{
			writeProfilesToStream(profiles, stream, ENCODING, versioner);
			String val;
			try
			{
				val = stream.toString(ENCODING);
			}
			catch (UnsupportedEncodingException e)
			{
				val = stream.toString();
			}
			return val;
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{ /* ignore */
			}
		}
	}

	public List<IProfile> readProfilesFromString(String profiles) throws CoreException
	{
		if (profiles != null && profiles.length() > 0)
		{
			byte[] bytes;
			try
			{
				bytes = profiles.getBytes(ENCODING);
			}
			catch (UnsupportedEncodingException e)
			{
				bytes = profiles.getBytes();
			}
			InputStream is = new ByteArrayInputStream(bytes);
			try
			{
				List<IProfile> res = readProfilesFromStream(new InputSource(is));
				if (res != null)
				{
					for (IProfile profile : res)
					{
						versioner.update(profile);
					}
				}
				return res;
			}
			finally
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{ /* ignore */
				}
			}
		}
		return null;
	}

	/**
	 * Read the available profiles from the internal XML file and return them as collection or <code>null</code> if the
	 * file is not a profile file.
	 * 
	 * @param file
	 *            The file to read from
	 * @return returns a list of <code>CustomProfile</code> or <code>null</code>
	 * @throws CoreException
	 */
	public List<IProfile> readProfilesFromFile(File file) throws CoreException
	{
		try
		{
			final FileInputStream reader = new FileInputStream(file);
			try
			{
				return readProfilesFromStream(new InputSource(reader));
			}
			finally
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{ /* ignore */
				}
			}
		}
		catch (IOException e)
		{
			throw createException(e, ProfilesMessages.ProfileStore_readingProblems);
		}
	}

	/**
	 * Load profiles from a XML stream and add them to a map or <code>null</code> if the source is not a profile store.
	 * 
	 * @param inputSource
	 *            The input stream
	 * @return returns a list of <code>CustomProfile</code> or <code>null</code>
	 * @throws CoreException
	 */
	protected List<IProfile> readProfilesFromStream(InputSource inputSource) throws CoreException
	{

		final ProfileDefaultHandler handler = new ProfileDefaultHandler();
		try
		{
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			final SAXParser parser = factory.newSAXParser();
			parser.parse(inputSource, handler);
		}
		catch (SAXException e)
		{
			throw createException(e, ProfilesMessages.ProfileStore_readingProblems);
		}
		catch (IOException e)
		{
			throw createException(e, ProfilesMessages.ProfileStore_readingProblems);
		}
		catch (ParserConfigurationException e)
		{
			throw createException(e, ProfilesMessages.ProfileStore_readingProblems);
		}
		return handler.getProfiles();
	}

	/**
	 * Write the available profiles to the internal XML file.
	 * 
	 * @param profiles
	 *            List of <code>CustomProfile</code>
	 * @param file
	 *            File to write
	 * @param encoding
	 *            the encoding to use
	 * @throws CoreException
	 */
	public void writeProfilesToFile(Collection<IProfile> profiles, File file) throws CoreException
	{
		final OutputStream stream;
		try
		{
			stream = new FileOutputStream(file);
			try
			{
				writeProfilesToStream(profiles, stream, ENCODING, versioner);
			}
			finally
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{ /* ignore */
				}
			}
		}
		catch (IOException e)
		{
			throw createException(e, ProfilesMessages.ProfileStore_serializingProblems);
		}
	}

	/**
	 * Save profiles to an XML stream
	 * 
	 * @param profiles
	 *            the list of <code>CustomProfile</code>
	 * @param stream
	 *            the stream to write to
	 * @param encoding
	 *            the encoding to use
	 * @throws CoreException
	 */
	public static void writeProfilesToStream(Collection<IProfile> profiles, OutputStream stream, String encoding,
			IProfileVersioner versioner) throws CoreException
	{

		try
		{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.newDocument();

			final Element rootElement = document.createElement(XML_NODE_ROOT);
			document.appendChild(rootElement);

			for (final IProfile profile : profiles)
			{
				if (!profile.isBuiltInProfile())
				{
					final Element profileElement = createProfileElement(profile, document, versioner);
					rootElement.appendChild(profileElement);
				}
			}

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			transformer.transform(new DOMSource(document), new StreamResult(stream));
		}
		catch (TransformerException e)
		{
			throw createException(e, ProfilesMessages.ProfileStore_serializingProblems);
		}
		catch (ParserConfigurationException e)
		{
			throw createException(e, ProfilesMessages.ProfileStore_serializingProblems);
		}
	}

	/*
	 * Create a new profile element in the specified document. The profile is not added to the document by this method.
	 */
	private static Element createProfileElement(IProfile profile, Document document, IProfileVersioner versioner)
	{
		final Element element = document.createElement(XML_NODE_PROFILE);
		element.setAttribute(XML_ATTRIBUTE_NAME, profile.getName());
		element.setAttribute(XML_ATTRIBUTE_VERSION, Integer.toString(profile.getVersion()));

		for (Map.Entry<String, String> entry : profile.getSettings().entrySet())
		{
			final String key = entry.getKey();
			final String value = entry.getValue();
			if (value != null)
			{
				final Element setting = document.createElement(XML_NODE_SETTING);
				setting.setAttribute(XML_ATTRIBUTE_ID, key);
				setting.setAttribute(XML_ATTRIBUTE_VALUE, value);
				element.appendChild(setting);
			}
			else
			{
				IdeLog.logError(FormatterPlugin.getDefault(),
						NLS.bind(ProfilesMessages.ProfileStore_noValueForKey, key), IDebugScopes.DEBUG);
			}
		}
		return element;
	}

	private IProfileVersioner versioner;

	/*
	 * Creates a UI exception for logging purposes
	 */
	private static CoreException createException(Throwable t, String message)
	{
		return new CoreException(new Status(IStatus.ERROR, FormatterPlugin.PLUGIN_ID, message, t));
	}
}
