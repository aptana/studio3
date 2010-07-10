/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.ui.preferences.formatter;

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
import java.util.Iterator;
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
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.preferences.formatter.ProfileManager.Profile;

/**
 * Can load/store profiles from/to profilesKey
 */
public class ProfileStore
{

	/** The default encoding to use */
	public static final String ENCODING = "UTF-8"; //$NON-NLS-1$

	/**
	 * 
	 */
	protected static final String VERSION_KEY_SUFFIX = ".version"; //$NON-NLS-1$

	/**
	 * A SAX event handler to parse the xml format for profiles.
	 */
	private final static class ProfileDefaultHandler extends DefaultHandler
	{

		private List<Profile> fProfiles;
		private int fVersion;

		private String fName;
		private Map<String, String> fSettings;
		private String fKind;

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
		 *      org.xml.sax.Attributes)
		 */
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
				fKind = attributes.getValue(XML_ATTRIBUTE_PROFILE_KIND);
				// if (fKind == null) //Can only be an CodeFormatterProfile created pre 3.3M2
				// fKind= ProfileVersioner.CODE_FORMATTER_PROFILE_KIND;

				fSettings = new HashMap<String, String>(200);

			}
			else if (qName.equals(XML_NODE_ROOT))
			{

				fProfiles = new ArrayList<Profile>();
				try
				{
					fVersion = Integer.parseInt(attributes.getValue(XML_ATTRIBUTE_VERSION));
				}
				catch (NumberFormatException ex)
				{
					throw new SAXException(ex);
				}

			}
		}

		/**
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		public void endElement(String uri, String localName, String qName)
		{
			if (qName.equals(XML_NODE_PROFILE))
			{
				fProfiles.add(new ProfileManager.CustomProfile(fName, fSettings, fVersion, fKind));
				fName = null;
				fSettings = null;
				fKind = null;
			}
		}

		/**
		 * @return List
		 */
		public List<Profile> getProfiles()
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
	private final static String XML_ATTRIBUTE_PROFILE_KIND = "kind"; //$NON-NLS-1$
	private final static String XML_ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$

	private final String fProfilesKey;
	private final String fProfilesVersionKey;

	private String pluginId;

	/**
	 * @param profilesKey
	 * @param pluginId
	 */
	public ProfileStore(String profilesKey, String pluginId)
	{
		fProfilesKey = profilesKey;
		fProfilesVersionKey = profilesKey + VERSION_KEY_SUFFIX;
		this.pluginId = pluginId;
	}

	/**
	 * @param scope
	 * @return Returns the collection of profiles currently stored in the preference store or <code>null</code> if the
	 *         loading failed. The elements are of type {@link ProfileManager.CustomProfile} and are all updated to the
	 *         latest version.
	 * @throws CoreException
	 */
	public List<Profile> readProfiles(IScopeContext scope) throws CoreException
	{
		return readProfilesFromString(scope.getNode(pluginId).get(fProfilesKey, null));
	}

	/**
	 * @param profiles
	 * @param instanceScope
	 * @throws CoreException
	 */
	public void writeProfiles(Collection<Profile> profiles, IScopeContext instanceScope) throws CoreException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream(2000);
		try
		{
			writeProfilesToStream(profiles, stream, ENCODING);
			String val;
			try
			{
				val = stream.toString(ENCODING);
			}
			catch (UnsupportedEncodingException e)
			{
				val = stream.toString();
			}
			IEclipsePreferences uiPreferences = instanceScope.getNode(pluginId);
			uiPreferences.put(fProfilesKey, val);
			uiPreferences.putInt(fProfilesVersionKey, 1);
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

	/**
	 * @param profiles
	 * @return List
	 * @throws CoreException
	 */
	public List<Profile> readProfilesFromString(String profiles) throws CoreException
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
				List<Profile> res = readProfilesFromStream(new InputSource(is));
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
	public List<Profile> readProfilesFromFile(File file) throws CoreException
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
			throw createException(e, FormatterMessages.CodingStyleConfigurationBlock_error_reading_xml_message);
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
	public static List<Profile> readProfilesFromStream(InputSource inputSource) throws CoreException
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
			throw createException(e, FormatterMessages.CodingStyleConfigurationBlock_error_reading_xml_message);
		}
		catch (IOException e)
		{
			throw createException(e, FormatterMessages.CodingStyleConfigurationBlock_error_reading_xml_message);
		}
		catch (ParserConfigurationException e)
		{
			throw createException(e, FormatterMessages.CodingStyleConfigurationBlock_error_reading_xml_message);
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
	public void writeProfilesToFile(Collection<Profile> profiles, File file, String encoding) throws CoreException
	{
		final OutputStream stream;
		try
		{
			stream = new FileOutputStream(file);
			try
			{
				writeProfilesToStream(profiles, stream, encoding);
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
			throw createException(e, FormatterMessages.CodingStyleConfigurationBlock_error_serializing_xml_message);
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
	public static void writeProfilesToStream(Collection<Profile> profiles, OutputStream stream, String encoding)
			throws CoreException
	{

		try
		{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.newDocument();

			final Element rootElement = document.createElement(XML_NODE_ROOT);
			rootElement.setAttribute(XML_ATTRIBUTE_VERSION, Integer.toString(1));

			document.appendChild(rootElement);

			for (final Iterator<Profile> iter = profiles.iterator(); iter.hasNext();)
			{
				final Profile profile = iter.next();
				if (profile.isProfileToSave())
				{
					final Element profileElement = createProfileElement(profile, document);
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
			throw createException(e, FormatterMessages.CodingStyleConfigurationBlock_error_serializing_xml_message);
		}
		catch (ParserConfigurationException e)
		{
			throw createException(e, FormatterMessages.CodingStyleConfigurationBlock_error_serializing_xml_message);
		}
	}

	/*
	 * Create a new profile element in the specified document. The profile is not added to the document by this method.
	 */
	private static Element createProfileElement(Profile profile, Document document)
	{
		final Element element = document.createElement(XML_NODE_PROFILE);
		element.setAttribute(XML_ATTRIBUTE_NAME, profile.getName());
		element.setAttribute(XML_ATTRIBUTE_VERSION, Integer.toString(profile.getVersion()));
		element.setAttribute(XML_ATTRIBUTE_PROFILE_KIND, ""); //$NON-NLS-1$

		final Iterator<String> keyIter = profile.getSettings().keySet().iterator();

		while (keyIter.hasNext())
		{
			final String key = keyIter.next();
			final String value = (String) profile.getSettings().get(key);
			if (value != null)
			{
				final Element setting = document.createElement(XML_NODE_SETTING);
				setting.setAttribute(XML_ATTRIBUTE_ID, key);
				setting.setAttribute(XML_ATTRIBUTE_VALUE, value);
				element.appendChild(setting);
			}
			else
			{
				UIEplPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, UIEplPlugin.PLUGIN_ID,
								"ProfileStore: Profile does not contain value for key " + key)); //$NON-NLS-1$
			}
		}
		return element;
	}

	/*
	 * Creates a UI exception for logging purposes
	 */
	private static CoreException createException(Throwable t, String message)
	{
		return new CoreException(new Status(IStatus.ERROR, UIEplPlugin.PLUGIN_ID, 0, message, t));
	}
}
