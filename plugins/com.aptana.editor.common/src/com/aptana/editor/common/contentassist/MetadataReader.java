/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.aptana.sax.Schema;
import com.aptana.sax.SchemaBuilder;
import com.aptana.sax.SchemaInitializationException;
import com.aptana.sax.ValidatingReader;

public abstract class MetadataReader extends ValidatingReader
{
	private boolean _bufferText;
	private StringBuffer _textBuffer = new StringBuffer();
	private Schema _metadataSchema;
	
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
	 * decodes HTML encoded strings
	 * 
	 * @param text
	 *            The text to decode
	 * @return The decoded text
	 */
	protected String decodeHtml(String text)
	{
		String textTemp = text.replaceAll("&amp;", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		
		textTemp = textTemp.replaceAll("&quot;", "\""); //$NON-NLS-1$//$NON-NLS-2$
		textTemp = textTemp.replaceAll("&lt;", "<"); //$NON-NLS-1$ //$NON-NLS-2$
		textTemp = textTemp.replaceAll("&gt;", ">"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return textTemp;
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
					this._schema = this._metadataSchema = SchemaBuilder.fromXML(schemaStream, this);
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
	 * Load the CSS metadata from the specified stream
	 * 
	 * @param stream
	 *            The input stream for the source XML
	 * @throws Exception
	 */
	public void loadXML(InputStream stream) throws Exception
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
				String msg = null;

				if (ex != null)
				{
					msg = MessageFormat.format(Messages.MetadataReader_ErrorParsingDocumentationXML, new Object[] { ex
							.getMessage() });
				}
				else
				{
					msg = MessageFormat.format(Messages.MetadataReader_ErrorParsingDocumentationXML, new Object[] { e
							.getMessage() });
				}

				Exception de = new Exception(msg, e);

				throw de;
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
			result = text.replaceAll("\\s+", " ").trim();
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
