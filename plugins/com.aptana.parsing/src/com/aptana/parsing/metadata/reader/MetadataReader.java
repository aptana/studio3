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
package com.aptana.parsing.metadata.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.aptana.parsing.metadata.ElementMetadata;
import com.aptana.parsing.metadata.EventMetadata;
import com.aptana.parsing.metadata.FieldMetadata;
import com.aptana.parsing.metadata.IMetadataItem;
import com.aptana.parsing.metadata.UserAgent;
import com.aptana.parsing.metadata.ValueMetadata;
import com.aptana.sax.SchemaBuilder;
import com.aptana.sax.SchemaInitializationException;
import com.aptana.sax.ValidatingReader;

/**
 * @author Kevin Lindsey
 */
public class MetadataReader extends ValidatingReader
{
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String METADATA_SCHEMA_XML = "/com/aptana/parsing/metadata/resources/MetadataSchema_1_0.xml"; //$NON-NLS-1$
	
	/*
	 * Fields
	 */
	private boolean _bufferText;
	private String _textBuffer;
	private ArrayList elements;
	private ArrayList _currentValues;
	private ElementMetadata _currentElement;
	private IMetadataItem _currentItem;
	private Hashtable _globalFields = new Hashtable();
	private Hashtable _globalEvents = new Hashtable();
	private ValueMetadata _currentValue;
	private UserAgent _currentUserAgent;

	/**
	 * The list of elements parsed
	 * 
	 * @return The list of elements
	 */
	public ArrayList getElements()
	{
		return elements;
	}

	/**
	 * These are fields which are defined once and referenced everywhere else (for compactness)
	 * 
	 * @return a hashtable of fields (name, FieldMetadata)
	 */
	public Hashtable getGlobalFields()
	{
		return _globalFields;
	}

	/**
	 * These are events which are defined once and referenced everywhere else (for compactness)
	 * 
	 * @return a hashtable of events (name, EventMetadata)
	 */
	public Hashtable getGlobalEvents()
	{
		return _globalEvents;
	}

	/*
	 * Properties
	 */

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of CoreLoader
	 * 
	 * @throws Exception
	 */
	public MetadataReader() throws Exception
	{
		elements = new ArrayList();

		// get schema for our documentation XML format
		InputStream schemaStream = MetadataReader.class.getResourceAsStream(METADATA_SCHEMA_XML);

		try
		{
			// create the schema
			this._schema = SchemaBuilder.fromXML(schemaStream, this);
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
			this._textBuffer += new String(buffer, offset, length);
		}
	}

	/**
	 * Load the JavaScript built-in objects documentation
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public void loadXML(String filename) throws Exception
	{
		FileInputStream fi = null;
		
		try
		{
			fi = new FileInputStream(filename);

			this.loadXML(fi);
		}
		catch (FileNotFoundException e)
		{
			String msg = MessageFormat.format(Messages.MetadataObjectsReader_UnableToLocateDocumentationXML, new Object[] {filename});
			Exception de = new Exception(msg, e);

			throw de;
		}
		finally
		{
			try
			{
				fi.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	/**
	 * Load the JavaScript built-in objects documentation using a stream.
	 * 
	 * @param stream
	 *            The input stream for the source XML
	 * @throws Exception
	 */
	public void loadXML(InputStream stream) throws Exception
	{
		// create a new SAX factory class
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);

		// clear properties
		this._textBuffer = EMPTY;
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
				msg = MessageFormat.format(Messages.MetadataReader_ErrorParsingDocumentationXML, new Object[] {ex.getMessage()});
			}
			else
			{
				msg = MessageFormat.format(Messages.MetadataReader_ErrorParsingDocumentationXML, new Object[] {e.getMessage()});
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
	 * decodes HTML encoded strings
	 * 
	 * @param text
	 *            The text to decode
	 * @return The decoded text
	 */
	public String decodeHtml(String text)
	{
		String textTemp = text.replaceAll("&amp;", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		textTemp = textTemp.replaceAll("&quot;", "\"");  //$NON-NLS-1$//$NON-NLS-2$
		textTemp = textTemp.replaceAll("&lt;", "<"); //$NON-NLS-1$ //$NON-NLS-2$
		textTemp = textTemp.replaceAll("&gt;", ">"); //$NON-NLS-1$ //$NON-NLS-2$
		return textTemp;
	}

	/**
	 * start processing a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterElement(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		ElementMetadata element = new ElementMetadata();
		elements.add(element);
		
		// grab and set property values
		String type = attributes.getValue("name"); //$NON-NLS-1$
		element.setName(type);

		String fullName = attributes.getValue("full-name"); //$NON-NLS-1$
		element.setFullName(fullName);
		
		// set current item
		this._currentElement = element;
	}

	/**
	 * Exit a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitElement(String ns, String name, String qname)
	{
		//System.out.println("MetadataReader._currentElement()" + _currentElement.getUserAgentPlatformNames().length);

		this._currentElement = null;
	}

	/**
	 * start processing a event
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterEvent(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		EventMetadata event = new EventMetadata();

		// grab and set property values
		String fieldName = attributes.getValue("name"); //$NON-NLS-1$
		event.setName(fieldName);

		String fieldType = attributes.getValue("type"); //$NON-NLS-1$
		event.setType(fieldType);

		if (_currentElement != null)
		{
			_currentElement.addEvent(event);
		}
		else
		{
			_globalEvents.put(fieldName, event);
		}

		// set current item
		this._currentItem = event;
	}

	/**
	 * Exit a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitEvent(String ns, String name, String qname)
	{
		//System.out.println("MetadataReader.exitEvent()" + _currentItem.getUserAgentPlatformNames().length);
		_currentValues = null;
		_currentItem = null;
	}

	/**
	 * start processing a class element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterField(String ns, String name, String qname, Attributes attributes)
	{
		// create a new item documentation object
		FieldMetadata field = new FieldMetadata();

		_currentValues = field.getValues();

		// grab and set property values
		String fieldName = attributes.getValue("name"); //$NON-NLS-1$
		field.setName(fieldName);

		String fieldType = attributes.getValue("type"); //$NON-NLS-1$
		field.setType(fieldType);

		String allowMultiple = attributes.getValue("allow-multiple-values"); //$NON-NLS-1$
		if(allowMultiple != null && allowMultiple.equals("true")) //$NON-NLS-1$
		{
			field.setAllowMultipleValues(true);
		}

		if (_currentElement != null)
		{
			_currentElement.addField(field);
		}
		else
		{
			_globalFields.put(fieldName, field);
		}

		// set current item
		this._currentItem = field;
	}

	/**
	 * Exit a field element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitField(String ns, String name, String qname)
	{
		//System.out.println("MetadataReader.exitField()" + _currentItem.getUserAgentPlatformNames().length);
		_currentValues = null;
		_currentItem = null;
	}

	/**
	 * Create a string by concatenating the elements of a string array using a
	 * delimited between each item
	 * 
	 * @param delimiter
	 *            The text to place between each element in the array
	 * @param items
	 *            The array of items to join
	 * @return The resulting string
	 */
	public static String join(String delimiter, String[] items)
	{
		int length = items.length;
		String result = EMPTY;

		if (length > 0)
		{
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < length - 1; i++)
			{
				sb.append(items[i]).append(delimiter);
			}

			sb.append(items[length - 1]);

			result = sb.toString();
		}

		return result;
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
		if (this._currentItem != null)
		{
			// add example to the current parameter
			this._currentItem.setDescription(decodeHtml(this._textBuffer));
		}
		else if(this._currentElement != null)
		{
			// add example to the current parameter
			this._currentElement.setDescription(decodeHtml(this._textBuffer));
		}
		else if(this._currentUserAgent != null)
		{
			// add example to the current parameter
			this._currentUserAgent.setDescription(decodeHtml(this._textBuffer));
		}

		// clear buffer and reset text buffering state
		this._textBuffer = EMPTY;
		this._bufferText = false;
	}

	/**
	 * Exit a deprecated element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitDeprecated(String ns, String name, String qname)
	{
		if (this._currentItem != null)
		{
			// add example to the current parameter
			this._currentItem.setDeprecatedDescription(decodeHtml(this._textBuffer));
		}
		else if(this._currentElement != null)
		{
			// add example to the current parameter
			this._currentElement.setDeprecatedDescription(decodeHtml(this._textBuffer));
		}

		// clear buffer and reset text buffering state
		this._textBuffer = EMPTY;
		this._bufferText = false;
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
		if (this._currentItem != null)
		{
			// add example to the current parameter
			this._currentItem.setHint(decodeHtml(this._textBuffer));
		}
		else if(this._currentElement != null)
		{
			// add example to the current parameter
			this._currentElement.setDescription(decodeHtml(this._textBuffer));
		}

		// clear buffer and reset text buffering state
		this._textBuffer = EMPTY;
		this._bufferText = false;
	}

	/**
	 * Exit a specification element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 * @param attributes
	 */
	public void enterSpecification(String ns, String name, String qname, Attributes attributes)
	{
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
		ValueMetadata field = new ValueMetadata();

		// grab and set property values
		String fieldName = attributes.getValue("name"); //$NON-NLS-1$
		field.setName(fieldName);
		
		String fieldType = attributes.getValue("description"); //$NON-NLS-1$
		field.setDescription(fieldType);

		this._currentValue = field;
	}

	/**
	 * Exit a field element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitValue(String ns, String name, String qname)
	{
		//System.out.println("MetadataReader.exitValue()" + _currentValue.getUserAgentPlatformNames().length);

		// add class to class list
		this._currentValues.add(this._currentValue);

		// clear current class
		this._currentValue = null;
	}
	
	/**
	 * Exit an availability element
	 * 
	 * @param ns
	 * @param name
	 * @param qname
	 */
	public void exitAvailability(String ns, String name, String qname)
	{
	}
	
	/**
	 * Exit a browser element
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
		else if (this._currentItem != null)
		{
			// add description to the current item
			this._currentItem.addUserAgent(this._currentUserAgent);
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
		UserAgent field = new UserAgent();
		
		String platform = attributes.getValue("platform"); //$NON-NLS-1$
		field.setPlatform(platform);
		
		String version = attributes.getValue("version"); //$NON-NLS-1$
		if(version != null)
		{
			field.setVersion(version);
		}

		String os = attributes.getValue("os"); //$NON-NLS-1$
		if(os != null)
		{
			field.setOs(os);
		}
		
		String osVersion = attributes.getValue("osVersion"); //$NON-NLS-1$
		if(osVersion != null)
		{
			field.setOsVersion(osVersion);
		}
				
		this._currentUserAgent = field;
	}
}