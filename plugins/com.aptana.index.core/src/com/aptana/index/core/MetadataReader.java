/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.aptana.core.util.StringUtil;
import com.aptana.sax.Schema;
import com.aptana.sax.SchemaBuilder;
import com.aptana.sax.SchemaInitializationException;
import com.aptana.sax.ValidatingReader;

public abstract class MetadataReader extends ValidatingReader
{
	private static final Map<String, String> ENTITY_MAP;
	private static final Pattern ENTITY_PATTERN;
	private static final Pattern LEADING_WHITESPACE = Pattern.compile("^\\s+"); //$NON-NLS-1$
	private static final Pattern TRAILING_WHITESPACE = Pattern.compile("\\s+$"); //$NON-NLS-1$
	private static final Pattern WHITESPACE = Pattern.compile("\\s+"); //$NON-NLS-1$

	private boolean _bufferText;
	private StringBuffer _textBuffer = new StringBuffer();
	private Schema _metadataSchema;

	/*
	 * static initializer
	 */
	static
	{
		// build entity map
		ENTITY_MAP = new HashMap<String, String>();

		ENTITY_MAP.put("&amp;", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		ENTITY_MAP.put("&quot;", "\""); //$NON-NLS-1$ //$NON-NLS-2$
		ENTITY_MAP.put("&lt;", "<"); //$NON-NLS-1$ //$NON-NLS-2$
		ENTITY_MAP.put("&gt;", ">"); //$NON-NLS-1$ //$NON-NLS-2$

		// build entity pattern
		List<String> entities = new ArrayList<String>();

		// add character entity pattern first
		entities.add("&(?:(\\\\d+)|x([a-zA-Z0-9]+));"); //$NON-NLS-1$

		// now add entity names
		for (String entity : ENTITY_MAP.keySet())
		{
			entities.add(Pattern.quote(entity));
		}

		ENTITY_PATTERN = Pattern.compile(StringUtil.join("|", entities)); //$NON-NLS-1$
	}

	/**
	 * Process character data
	 * 
	 * @param buffer
	 * @param offset
	 * @param length
	 */
	public void characters(char[] buffer, int offset, int length)
	{
		if (this._bufferText)
		{
			this._textBuffer.append(new String(buffer, offset, length));
		}
	}

	/**
	 * getSchemaStream
	 * 
	 * @return
	 */
	protected abstract InputStream getSchemaStream();

	/**
	 * getText
	 * 
	 * @return
	 */
	protected String getText()
	{
		String result = this._textBuffer.toString();

		// clear buffer and reset text buffering state
		this._textBuffer.setLength(0);
		this._bufferText = false;

		return result;
	}

	/**
	 * isBufferingText
	 * 
	 * @return
	 */
	public boolean isBufferingText()
	{
		return this._bufferText;
	}

	/**
	 * loadMetadataSchema
	 * 
	 * @throws Exception
	 */
	private void loadMetadataSchema() throws Exception
	{
		if (this._metadataSchema == null)
		{
			// get schema for our documentation XML format
			InputStream schemaStream = this.getSchemaStream();

			if (schemaStream != null)
			{
				try
				{
					// create the schema
					this._schema = this._metadataSchema = SchemaBuilder.fromXML(schemaStream);
				}
				catch (SchemaInitializationException e)
				{
					String msg = Messages.MetadataReader_ErrorLoadingDocumentationXML;
					Exception ie = new Exception(msg, e);

					throw ie;
				}
				finally
				{
					// close the input stream
					try
					{
						schemaStream.close();
					}
					catch (IOException e)
					{
						String msg = Messages.MetadataReader_IOErrorProcessingDocumentationXML;
						Exception ie = new Exception(msg, e);

						throw ie;
					}
				}
			}
		}
	}

	/**
	 * Load metadata from the specified stream
	 * 
	 * @param stream
	 *            The input stream for the source XML
	 * @throws Exception
	 */
	public void loadXML(InputStream stream) throws Exception
	{
		loadXML(stream, null);
	}

	/**
	 * Load metadata from the specified stream. The input name is used in error messages to indicate what resource is
	 * being loaded
	 * 
	 * @param stream
	 *            The input stream for the source XML
	 * @param inputName
	 *            The name of the resource being processed
	 * @throws Exception
	 */
	public void loadXML(InputStream stream, String inputName) throws Exception
	{
		this.loadMetadataSchema();

		if (this._metadataSchema != null)
		{
			// create a new SAX factory class
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);

			// clear properties
			this.getText();
			SAXParser saxParser = null;

			// parse the XML file
			try
			{
				saxParser = factory.newSAXParser();
				saxParser.parse(stream, this);
			}
			catch (ParserConfigurationException e)
			{
				String msg = Messages.MetadataReader_SAXParserConfiguredIncorrectly;
				Exception de = new Exception(msg, e);

				throw de;
			}
			catch (SAXException e)
			{
				Exception ex = e.getException();

				if (StringUtil.isEmpty(inputName))
				{
					inputName = "the documentation XML file"; //$NON-NLS-1$
				}

				// @formatter:off
				String msg = MessageFormat.format(
					Messages.MetadataReader_ErrorParsingDocumentationXML,
					inputName,
					(ex != null) ? ex.getMessage() : e.getMessage()
				);
				// @formatter:on

				throw new Exception(msg, e);
			}
			catch (IOException e)
			{
				String msg = Messages.MetadataObjectsReader_IOErrorOccurredProcessingDocumentationXML;
				Exception de = new Exception(msg, e);

				throw de;
			}
		}
	}

	/**
	 * normalizeText
	 * 
	 * @param text
	 * @return
	 */
	public String normalizeText(String text)
	{
		String result = null;

		if (text != null)
		{
			result = LEADING_WHITESPACE.matcher(text).replaceAll(StringUtil.EMPTY);
			result = TRAILING_WHITESPACE.matcher(result).replaceAll(StringUtil.EMPTY);
			result = WHITESPACE.matcher(result).replaceAll(" "); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * numericStringToString
	 * 
	 * @param text
	 * @param base
	 * @return
	 */
	private String numericStringToString(String text, int base)
	{
		String result = null;

		if (text != null && text.length() > 0)
		{
			try
			{
				int charCode = Integer.parseInt(text, base);
				char[] chars = Character.toChars(charCode);

				result = new String(chars);
			}
			catch (NumberFormatException e)
			{
				// that string wasn't parse-able, so just return the origin text
				result = text;
			}
		}

		return result;
	}

	/**
	 * Resolve HTML entities to their string values
	 * 
	 * @param text
	 *            The text to decode
	 * @return The decoded text
	 */
	protected String resolveEntities(String text)
	{
		String result = text;

		if (text != null && text.length() > 0)
		{
			// replace named entities
			StringBuffer buffer = new StringBuffer();
			Matcher m = ENTITY_PATTERN.matcher(text);

			while (m.find())
			{
				// try group one as a decimal-encoded character
				String replaceText = this.numericStringToString(m.group(1), 10);

				if (replaceText == null)
				{
					// try group two as hexadecimal-encoded character
					replaceText = this.numericStringToString(m.group(2), 16);
				}

				if (replaceText == null)
				{
					// try whatever matched as an entity name
					replaceText = ENTITY_MAP.get(m.group());
				}

				if (replaceText == null)
				{
					// this should never happen, but fall back to original text in case all conversions failed
					replaceText = m.group();
				}

				// append result
				m.appendReplacement(buffer, replaceText);
			}

			// append remaining unmatched text
			m.appendTail(buffer);

			result = buffer.toString();
		}

		return result;
	}

	/**
	 * start buffering text
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void startTextBuffer()
	{
		this._bufferText = true;
	}

	/**
	 * start buffering text
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void startTextBuffer(String ns, String name, String qname, Attributes attributes)
	{
		this.startTextBuffer();
	}
}
