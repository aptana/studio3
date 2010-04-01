/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.sax;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Kevin Lindsey
 */
public class NamespaceSniffer extends DefaultHandler
{
	private String _namespace;
	
	/**
	 * NamespaceSniffer
	 */
	public NamespaceSniffer()
	{
	}
	
	/**
	 * getNamespace
	 * 
	 * @return
	 */
	public String getNamespace()
	{
		return this._namespace;
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
	public void read(InputStream in) throws ParserConfigurationException, IOException
	{
		// create a new SAX factory class
		SAXParserFactory factory = SAXParserFactory.newInstance();

		// make sure it generates namespace aware parsers
		factory.setNamespaceAware(true);

		try
		{
			// create the parser
			SAXParser saxParser = factory.newSAXParser();

			// parse the XML
			saxParser.parse(in, this);
		}
		catch (SAXException e)
		{
			// do nothing
		}
		finally
		{
			// close stream
			in.close();
		}
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
	 *      org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		this._namespace = uri;
		
		throw new SAXException();
	}
}
