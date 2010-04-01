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
package com.aptana.sax;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class AttributeSniffer extends DefaultHandler
{

	private String targetElement;
	private String targetAttribute;
	private String matchedValue;

	/**
	 * Creates a new attribute sniffer for an element name and attribute name
	 * 
	 * @param elementName
	 * @param attributeName
	 */
	public AttributeSniffer(String elementName, String attributeName)
	{
		this.targetAttribute = attributeName;
		this.targetElement = elementName;
	}

	/**
	 * Load a xml file and validate it against this reader's schema
	 * 
	 * @param filename
	 *            The name of the xml file to load and validate
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public void read(String filename) throws ParserConfigurationException, SAXException, IOException
	{
		FileInputStream fi = new FileInputStream(filename);
		this.read(fi);
		fi.close();
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

		// parse the XML
		saxParser.parse(in, this);

		// close stream
		in.close();
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
	 *      org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if (localName.equals(targetElement))
		{
			if (attributes.getValue(targetAttribute) != null)
			{
				this.matchedValue = attributes.getValue(targetAttribute);
			}
		}
	}

	/**
	 * Gets the value sniffed
	 * 
	 * @return - matched value
	 */
	public String getMatchedValue()
	{
		return this.matchedValue;
	}

}
