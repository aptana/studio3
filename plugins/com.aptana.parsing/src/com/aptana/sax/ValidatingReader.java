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
package com.aptana.sax;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Kevin Lindsey
 */
public class ValidatingReader extends DefaultHandler
{
	/**
	 * The schema associated with this reader
	 */
	protected Schema _schema;

	/**
	 * Create a new instance of ValidatingReader
	 */
	protected ValidatingReader()
	{
		this(null);
	}

	/**
	 * Create a new instance of ValidatingReader
	 * 
	 * @param schema
	 *            The schema to associate with this reader.
	 */
	public ValidatingReader(Schema schema)
	{
		// make sure we have a valid schema
		if (schema == null)
		{
			schema = new Schema(this);
		}

		this._schema = schema;
	}

	/**
	 * Finish processing the specified element
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 * @throws SAXException
	 */
	public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException
	{
		if (this._schema != null)
		{
			try
			{
				this._schema.exitElement(namespaceURI, localName, qualifiedName);
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXException(e);
			}
			catch (IllegalAccessException e)
			{
				throw new SAXException(e);
			}
			catch (InvocationTargetException e)
			{
				throw new SAXException(e);
			}
		}
	}

	/**
	 * Load an XML stream and validate it against this reader's schema
	 * 
	 * @param in
	 *            The input stream that contains XML
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public void read(InputStream in) throws ParserConfigurationException, SAXException, IOException
	{
		// create a new SAX factory class
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		// make sure it generates namespace aware parsers
		factory.setNamespaceAware(true);

		// create the parser
		SAXParser saxParser = factory.newSAXParser();
		
		//saxParser.getXMLReader().setFeature("http://xml.org/sax/features/validation", false);
		//saxParser.setProperty("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", Boolean.FALSE);
		
		// associate our custom entity resolver
		//XMLReader reader = saxParser.getXMLReader();
		//reader.setEntityResolver(new SimpleResolver());
		
		// parse the XML
		saxParser.parse(in, this);
	}

	/**
	 * startDocument handler
	 * 
	 * @throws SAXException
	 */
	public void startDocument() throws SAXException
	{
		if (this._schema != null)
		{
			this._schema.reset();
		}
	}

	/**
	 * Handle the start of a new element
	 * 
	 * @param namespaceURI
	 *            The element's namespace URI
	 * @param localName
	 *            The element's local name
	 * @param qualifiedName
	 *            The element's qualified name
	 * @param attributes
	 *            The element's attributes
	 * @throws SAXException
	 */
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes)
			throws SAXException
	{
		if (this._schema != null)
		{
			try
			{
				this._schema.moveTo(namespaceURI, localName, qualifiedName, attributes);
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXException(e);
			}
			catch (InvalidTransitionException e)
			{
				throw new SAXException(e);
			}
			catch (IllegalAccessException e)
			{
				throw new SAXException(e);
			}
			catch (InvocationTargetException e)
			{
				Throwable innerException = e.getCause();
				
				if (innerException instanceof SAXException)
				{
					throw (SAXException) innerException;
				}
				else
				{
					throw new SAXException(e);
				}
			}
		}
	}
}
