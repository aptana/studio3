/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.sax;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.aptana.core.logging.IdeLog;
import com.aptana.parsing.ParsingPlugin;

/**
 * @author Kevin Lindsey
 */
public class ValidatingReader extends DefaultHandler
{
	/**
	 * The schema associated with this reader
	 */
	protected Schema _schema;
	protected IValidatingReaderLogger _logger;
	protected Locator _locator;

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
			schema = new Schema(); // $codepro.audit.disable questionableAssignment
		}

		this._schema = schema;
	}

	/**
	 * attributesToMap
	 * 
	 * @param attributes
	 * @return
	 */
	protected Map<String, String> attributesToMap(Attributes attributes, boolean trim)
	{
		Map<String, String> result = new HashMap<String, String>();

		if (attributes != null)
		{
			for (int i = 0; i < attributes.getLength(); i++)
			{
				String name = attributes.getLocalName(i);
				String value = trim ? attributes.getValue(i).trim() : attributes.getValue(i);

				result.put(name, value);
			}
		}

		return result;
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
				throw new SAXException(e); // $codepro.audit.disable exceptionUsage.exceptionCreation
			}
			catch (IllegalAccessException e)
			{
				throw new SAXException(e); // $codepro.audit.disable exceptionUsage.exceptionCreation
			}
			catch (InvocationTargetException e)
			{
				throw new SAXException(e); // $codepro.audit.disable exceptionUsage.exceptionCreation
			}
		}
	}

	/**
	 * logError
	 * 
	 * @param message
	 */
	public void logError(String message)
	{
		if (this._logger != null && this._locator != null)
		{
			this._logger.logError(message, this._locator.getLineNumber(), 0);
		}
		else
		{
			IdeLog.logError(ParsingPlugin.getDefault(), message);
		}
	}

	/**
	 * logInfo
	 * 
	 * @param message
	 */
	public void logInfo(String message)
	{
		if (this._logger != null)
		{
			this._logger.logInfo(message, this._locator.getLineNumber(), 0);
		}
		else
		{
			IdeLog.logInfo(ParsingPlugin.getDefault(), message);
		}
	}

	/**
	 * logWarning
	 * 
	 * @param message
	 */
	public void logWarning(String message)
	{
		if (this._logger != null)
		{
			this._logger.logWarning(message, this._locator.getLineNumber(), 0);
		}
		else
		{
			IdeLog.logWarning(ParsingPlugin.getDefault(), message);
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

		// saxParser.getXMLReader().setFeature("http://xml.org/sax/features/validation", false);
		// saxParser.setProperty("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", Boolean.FALSE);

		// associate our custom entity resolver
		// XMLReader reader = saxParser.getXMLReader();
		// reader.setEntityResolver(new SimpleResolver());

		// parse the XML
		saxParser.parse(in, this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setDocumentLocator(Locator locator)
	{
		super.setDocumentLocator(locator);

		this._locator = locator;
	}

	/**
	 * setLogger
	 * 
	 * @param logger
	 */
	public void setLogger(IValidatingReaderLogger logger)
	{
		this._logger = logger;
	}

	/**
	 * startDocument handler
	 * 
	 * @throws SAXException
	 */
	public void startDocument()
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
				throw new SAXException(e); // $codepro.audit.disable exceptionUsage.exceptionCreation
			}
			catch (InvalidTransitionException e)
			{
				throw new SAXException(e); // $codepro.audit.disable exceptionUsage.exceptionCreation
			}
			catch (IllegalAccessException e)
			{
				throw new SAXException(e); // $codepro.audit.disable exceptionUsage.exceptionCreation
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
					throw new SAXException(e); // $codepro.audit.disable exceptionUsage.exceptionCreation
				}
			}
		}
	}
}
