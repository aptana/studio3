/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.editor.css.contentassist.index;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.SpecificationElement;
import com.aptana.editor.css.contentassist.model.UserAgentElement;
import com.aptana.editor.css.contentassist.model.ValueElement;
import com.aptana.sax.Schema;
import com.aptana.sax.SchemaBuilder;
import com.aptana.sax.SchemaInitializationException;
import com.aptana.sax.ValidatingReader;

/**
 * @author Kevin Lindsey
 */
public class CSSMetadataReader extends ValidatingReader
{
	private static final String METADATA_SCHEMA_XML = "/metadata/CSSMetadataSchema.xml"; //$NON-NLS-1$
	
	private boolean _bufferText;
	private StringBuffer _textBuffer = new StringBuffer();
	private List<ElementElement> _elements = new LinkedList<ElementElement>();
	private ElementElement _currentElement;
	private PropertyElement _currentProperty;
	private ValueElement _currentValue;
	private UserAgentElement _currentUserAgent;
	private List<PropertyElement> _properties = new LinkedList<PropertyElement>();
	private Schema _metadataSchema;

	/**
	 * CSSMetadataReader
	 */
	public CSSMetadataReader()
	{
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
	 * decodes HTML encoded strings
	 * 
	 * @param text
	 *            The text to decode
	 * @return The decoded text
	 */
	public String decodeHtml(String text)
	{
		String textTemp = text.replaceAll("&amp;", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		
		textTemp = textTemp.replaceAll("&quot;", "\""); //$NON-NLS-1$//$NON-NLS-2$
		textTemp = textTemp.replaceAll("&lt;", "<"); //$NON-NLS-1$ //$NON-NLS-2$
		textTemp = textTemp.replaceAll("&gt;", ">"); //$NON-NLS-1$ //$NON-NLS-2$
		
		return textTemp;
	}

	/**
	 * start processing a browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterBrowser(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		UserAgentElement userAgent = new UserAgentElement();

		userAgent.setPlatform(attributes.getValue("platform"));
		userAgent.setVersion(attributes.getValue("version"));
		userAgent.setOS(attributes.getValue("os"));
		userAgent.setOSVersion(attributes.getValue("osVersion"));

		this._currentUserAgent = userAgent;
	}

	/**
	 * start processing an element element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterElement(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		ElementElement element = new ElementElement();
		
		// grab and set property values
		element.setName(attributes.getValue("name"));
		element.setDisplayName(attributes.getValue("display-name"));

		// set current item
		this._currentElement = element;
	}
	
	/**
	 * start processing a property element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterProperty(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		PropertyElement property = new PropertyElement();

		// grab and set property values
		property.setName(attributes.getValue("name"));
		property.setType(attributes.getValue("type"));
		property.setAllowMultipleValues(attributes.getValue("allow-multipe-values"));

		// set current item
		this._currentProperty = property;
	}
	
	/**
	 * start processing a property reference element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterPropertyReference(String ns, String name, String qname, Attributes attributes)
	{
		this._currentElement.addProperty(attributes.getValue("name"));
	}

	/**
	 * start processing a specification element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterSpecification(String ns, String name, String qname, Attributes attributes)
	{
		SpecificationElement specification = new SpecificationElement();
		
		specification.setName(attributes.getValue("name"));
		specification.setVersion(attributes.getValue("version"));
		
		this._currentProperty.addSpecification(specification);
	}

	/**
	 * start processing a value element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterValue(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		ValueElement value = new ValueElement();

		// grab and set property values
		value.setName(attributes.getValue("name"));
		value.setDescription(attributes.getValue("description"));

		this._currentValue = value;
	}

	/**
	 * start processing a browser element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitBrowser(String ns, String name, String qname)
	{
		if (this._currentValue != null)
		{
			// add description to the current value
			this._currentValue.addUserAgent(this._currentUserAgent);
		}
		else if (this._currentProperty != null)
		{
			// add description to the current item
			this._currentProperty.addUserAgent(this._currentUserAgent);
		}
		else if (this._currentElement != null)
		{
			// add example to the current element
			this._currentElement.addUserAgent(this._currentUserAgent);
		}

		// clear current class
		this._currentUserAgent = null;
	}

	/**
	 * Exit a description element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitDescription(String ns, String name, String qname)
	{
		String text = this._textBuffer.toString();
		
		if (this._currentProperty != null)
		{
			// add example to the current parameter
			this._currentProperty.setDescription(this.decodeHtml(text));
		}
		else if (this._currentElement != null)
		{
			// add example to the current parameter
			this._currentElement.setDescription(this.decodeHtml(text));
		}
		else if (this._currentUserAgent != null)
		{
			// add example to the current parameter
			this._currentUserAgent.setDescription(this.decodeHtml(text));
		}

		// clear buffer and reset text buffering state
		this.stopTextBuffer();
	}

	/**
	 * Exit an element element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitElement(String ns, String name, String qname)
	{
		this._elements.add(this._currentElement);
		
		this._currentElement = null;
	}

	/**
	 * exit an example element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitExample(String ns, String name, String qname)
	{
		String text = this._textBuffer.toString();
		
		if (this._currentProperty != null)
		{
			this._currentProperty.setExample(this.decodeHtml(text));
		}
		else if (this._currentElement != null)
		{
			this._currentElement.setExample(this.decodeHtml(text));
		}
		
		this.stopTextBuffer();
	}
	
	/**
	 * Exit a property element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitProperty(String ns, String name, String qname)
	{
		this._properties.add(this._currentProperty);
		
		this._currentProperty = null;
	}

	/**
	 * exit a remarks element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitRemarks(String ns, String name, String qname)
	{
		String text = this._textBuffer.toString();
		
		if (this._currentProperty != null)
		{
			this._currentProperty.setRemark(text);
		}
		else if (this._currentElement != null)
		{
			this._currentElement.setRemark(text);
		}
		
		this.stopTextBuffer();
	}
	
	/**
	 * Exit a hint element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitHint(String ns, String name, String qname)
	{
		String text = this._textBuffer.toString();
		
		if (this._currentProperty != null)
		{
			// add hint to the current property
			this._currentProperty.setHint(this.decodeHtml(text));
		}
		else if (this._currentElement != null)
		{
			// add hint to the current element
			this._currentElement.setDescription(this.decodeHtml(text));
		}

		// clear buffer and reset text buffering state
		this.stopTextBuffer();
	}

	/**
	 * Exit a value element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitValue(String ns, String name, String qname)
	{
		// add class to class list
		this._currentProperty.addValue(this._currentValue);

		// clear current class
		this._currentValue = null;
	}

	/**
	 * getElements
	 * 
	 * @return
	 */
	public List<ElementElement> getElements()
	{
		return this._elements;
	}
	
	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		return this._properties;
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
			InputStream schemaStream = CSSMetadataReader.class.getResourceAsStream(METADATA_SCHEMA_XML);

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
			this.stopTextBuffer();
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
	 * start buffering text
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void startTextBuffer(String ns, String name, String qname, Attributes attributes)
	{
		this._bufferText = true;
	}
	
	/**
	 * stop buffering text
	 */
	protected void stopTextBuffer()
	{
		// clear buffer and reset text buffering state
		this._textBuffer.setLength(0);
		this._bufferText = false;
	}
}