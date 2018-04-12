/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.sax;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.aptana.core.logging.IdeLog;
import com.aptana.parsing.ParsingPlugin;

/**
 * @author Kevin Lindsey
 */
public final class SchemaBuilder extends ValidatingReader
{

	private enum Element
	{
		USE_ELEMENT_SET("use-element-set"), //$NON-NLS-1$
		ELEMENT_SET("element-set"), //$NON-NLS-1$
		SETS("sets"), //$NON-NLS-1$
		CHILD_ELEMENT("child-element"), //$NON-NLS-1$
		ATTRIBUTE("attribute"), //$NON-NLS-1$
		ELEMENT("element"), //$NON-NLS-1$
		SCHEMA("schema"), //$NON-NLS-1$
		UNDEFINED(null);

		private String name;

		private Element(String name)
		{
			this.name = name;
		}

		private static Element fromString(String name)
		{
			if (name != null)
			{
				for (Element b : Element.values())
				{
					if (name.equals(b.name))
					{
						return b;
					}
				}
			}
			return UNDEFINED;
		}
	}

	private static String OPTIONAL = "optional"; //$NON-NLS-1$
	private static String REQUIRED = "required"; //$NON-NLS-1$
	private static String USAGE_ATTRIBUTE = "usage"; //$NON-NLS-1$
	private static String HAS_TEXT_ATTRIBUTE = "hasText"; //$NON-NLS-1$
	private static String ON_EXIT_ATTRIBUTE = "onExit"; //$NON-NLS-1$
	private static String ON_ENTER_ATTRIBUTE = "onEnter"; //$NON-NLS-1$
	private static String TYPE_ATTRIBUTE = "type"; //$NON-NLS-1$
	private static String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
	private static String ALLOW_FREEFORM_MARKUP_ATTRIBUTE = "allowFreeformMarkup"; //$NON-NLS-1$

	private static String SCHEMA_1_0_NAMESPACE = "http://www.aptana.com/2005/schema/1.0"; //$NON-NLS-1$
	private static String SCHEMA_1_1_NAMESPACE = "http://www.aptana.com/2007/schema/1.1"; //$NON-NLS-1$

	private Schema _newSchema;
	private Stack<ISchemaElement> _elementStack; // $codepro.audit.disable declareAsInterface
	private ISchemaElement _currentElement;

	private String _currentSetId;
	private Map<String, ISchemaElement> _sets;

	private Schema _versionSelectorSchema;
	private Schema _schema10;
	private Schema _schema11;

	/**
	 * Create a new instance of SchemaParser
	 * 
	 * @param schema
	 */
	private SchemaBuilder()
	{
		this._elementStack = new Stack<ISchemaElement>();
		this._sets = new HashMap<String, ISchemaElement>();

		buildSchemaSchemas();
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes)
			throws SAXException
	{
		super.startElement(namespaceURI, localName, qualifiedName, attributes);

		switch (Element.fromString(localName))
		{
			case SCHEMA:
				startSchemaElement(namespaceURI, localName, qualifiedName, attributes);
				break;

			case ELEMENT:
				startElementElement(namespaceURI, localName, qualifiedName, attributes);
				break;

			case ATTRIBUTE:
				startAttributeElement(namespaceURI, localName, qualifiedName, attributes);
				break;

			case CHILD_ELEMENT:
				startChildElementElement(namespaceURI, localName, qualifiedName, attributes);
				break;

			case ELEMENT_SET:
				startElementSetElement(namespaceURI, localName, qualifiedName, attributes);
				break;

			case USE_ELEMENT_SET:
				startUseElementSetElement(namespaceURI, localName, qualifiedName, attributes);
				break;

			case UNDEFINED:
				IdeLog.logWarning(ParsingPlugin.getDefault(),
						MessageFormat.format("Unable to convert element with name {0} to enum value", localName)); //$NON-NLS-1$
				break;

			default:
				// do nothing
				break;
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException
	{
		switch (Element.fromString(localName))
		{
			case ELEMENT:
				exitElementElement(namespaceURI, localName, qualifiedName);
				break;

			case ELEMENT_SET:
				exitElementSetElement(namespaceURI, localName, qualifiedName);
				break;
			case UNDEFINED:
				IdeLog.logWarning(ParsingPlugin.getDefault(),
						MessageFormat.format("Unable to convert element with name {0} to enum value", localName)); //$NON-NLS-1$
				break;

			default:
				// do nothing
				break;
		}
		super.endElement(namespaceURI, localName, qualifiedName);
	}

	/**
	 * Create the state machine that loads and recognizes our schema xml format
	 */
	private void buildSchemaSchemas()
	{
		// create 1.0 schema schema
		this._schema10 = this.buildSchema10Schema();

		// create 1.1 schema schema
		this._schema11 = this.buildSchema11Schema();

		// create schema schema to select the proper version schema
		this._versionSelectorSchema = this.buildVersionSelectorSchema();
	}

	/**
	 * buildVersionSelectorSchema
	 */
	private Schema buildVersionSelectorSchema()
	{
		Schema result = new Schema();

		// create single element schema used to detect which schema version to use
		// to process the current schema definition file

		// tell schema this is the root
		result.setRootElement(Element.SCHEMA.name);

		return result;
	}

	/**
	 * buildSchema10Schema
	 */
	private Schema buildSchema10Schema()
	{
		Schema result = new Schema();

		// create the root element
		ISchemaElement root = result.createElement(Element.SCHEMA.name);

		// tell schema this is the root
		result.setRootElement(Element.SCHEMA.name);

		// create element element and add as child of root
		ISchemaElement element = result.createElement(Element.ELEMENT.name);
		root.addTransition(element);

		// set element's attributes
		element.addAttribute(NAME_ATTRIBUTE, REQUIRED);
		element.addAttribute(TYPE_ATTRIBUTE, OPTIONAL);
		element.addAttribute(ON_ENTER_ATTRIBUTE, OPTIONAL);
		element.addAttribute(ON_EXIT_ATTRIBUTE, OPTIONAL);
		element.addAttribute(HAS_TEXT_ATTRIBUTE, OPTIONAL);
		element.addAttribute(ALLOW_FREEFORM_MARKUP_ATTRIBUTE, OPTIONAL);

		// create attribute element and add as child of element
		ISchemaElement attribute = result.createElement(Element.ATTRIBUTE.name);
		element.addTransition(attribute);

		// set attribute's attributes
		attribute.addAttribute(NAME_ATTRIBUTE, REQUIRED);
		attribute.addAttribute(USAGE_ATTRIBUTE, OPTIONAL);

		// create child-element element and add as child of element
		ISchemaElement childElement = result.createElement(Element.CHILD_ELEMENT.name);
		element.addTransition(childElement);

		// set child-element's attributes
		childElement.addAttribute(NAME_ATTRIBUTE, REQUIRED);

		return result;
	}

	/**
	 * buildSchema10Schema
	 */
	private Schema buildSchema11Schema()
	{
		Schema result = new Schema();

		// create the root element
		ISchemaElement root = result.createElement(Element.SCHEMA.name);

		// tell schema this is the root
		result.setRootElement(Element.SCHEMA.name);

		// create element element and add as child of root
		ISchemaElement element = result.createElement(Element.ELEMENT.name);
		root.addTransition(element);

		// set element's attributes
		element.addAttribute(NAME_ATTRIBUTE, REQUIRED);
		element.addAttribute(ON_ENTER_ATTRIBUTE, OPTIONAL);
		element.addAttribute(ON_EXIT_ATTRIBUTE, OPTIONAL);
		element.addAttribute(HAS_TEXT_ATTRIBUTE, OPTIONAL);

		// allow element to transition to self
		element.addTransition(element);

		// create attribute element and add as child of element
		ISchemaElement attribute = result.createElement(Element.ATTRIBUTE.name);
		element.addTransition(attribute);

		// set attribute's attributes
		attribute.addAttribute(NAME_ATTRIBUTE, REQUIRED);
		attribute.addAttribute(USAGE_ATTRIBUTE, OPTIONAL);

		// create sets element and add as child of root
		ISchemaElement sets = result.createElement(Element.SETS.name);
		root.addTransition(sets);

		// create element-set element and add as child of sets element
		ISchemaElement elementSet = result.createElement(Element.ELEMENT_SET.name);
		sets.addTransition(elementSet);

		// set elementSet's attributes
		elementSet.addAttribute("id", REQUIRED); //$NON-NLS-1$

		// add element as child of element-set
		elementSet.addTransition(element);

		// create use-element-set element and add as child of root, element, and element-set
		ISchemaElement useElementSet = result.createElement(Element.USE_ELEMENT_SET.name);
		root.addTransition(useElementSet);
		element.addTransition(useElementSet);
		elementSet.addTransition(useElementSet);

		// add useElementSet's attributes
		useElementSet.addAttribute(NAME_ATTRIBUTE, REQUIRED);

		return result;
	}

	/**
	 * Finish processing the specified element
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 */
	public void exitElementElement(String namespaceURI, String localName, String qualifiedName)
	{
		// restore parent element as the new current element
		this._currentElement = this._elementStack.pop();
	}

	/**
	 * Finish processing the specified element-set
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 */
	public void exitElementSetElement(String namespaceURI, String localName, String qualifiedName)
	{
		// save set
		this._sets.put(this._currentSetId, this._currentElement);

		// clear name
		this._currentSetId = null;

		// restore parent element as the new current element
		this._currentElement = this._elementStack.pop();
	}

	/**
	 * Load an xml schema that describes and recognizes a specific xml format
	 * 
	 * @param filename
	 *            The name of the xml schema file to load
	 * @param handler
	 *            The handler to use for event callbacks
	 * @return A validating XML reader that will recognize and validate against the loaded schema
	 * @throws SchemaInitializationException
	 */
	public static Schema fromXML(String filename) throws SchemaInitializationException
	{
		FileInputStream fi = null;
		Schema schema = null;

		try
		{
			fi = new FileInputStream(filename);

			schema = fromXML(fi);
		}
		catch (FileNotFoundException e)
		{
			String msg = Messages.SchemaBuilder_File_Unlocatable + filename;
			SchemaInitializationException ie = new SchemaInitializationException(msg, e);

			throw ie;
		}
		finally
		{
			if (fi != null)
			{
				try
				{
					fi.close();
				}
				catch (IOException e) // $codepro.audit.disable emptyCatchClause
				{
				}
			}
		}

		return schema;
	}

	/**
	 * Load an xml schema that describes and recognizes a specific xml format
	 * 
	 * @param in
	 *            The input stream of xml schema data
	 * @param handler
	 *            The handler to use for event callbacks
	 * @return A validating XML reader that will recognize and validate against the loaded schema
	 * @throws SchemaInitializationException
	 */
	public static Schema fromXML(InputStream in) throws SchemaInitializationException
	{
		Schema result = new Schema();

		SchemaBuilder builder = new SchemaBuilder();

		// setup selector schema and reset in case it has been used previously
		builder._schema = builder._versionSelectorSchema;
		builder._schema.reset();

		// create new schema and associate with resulting reader
		builder._newSchema = result;

		// build new schema from XML description
		try
		{
			builder.read(in);
		}
		catch (ParserConfigurationException e)
		{
			String msg = Messages.SchemaBuilder_SAX_Parser_Initialization_Error;
			SchemaInitializationException ie = new SchemaInitializationException(msg, e);

			throw ie;
		}
		catch (SAXException e)
		{
			String msg = Messages.SchemaBuilder_SAX_Parser_Error;
			SchemaInitializationException ie = new SchemaInitializationException(msg, e);

			throw ie;
		}
		catch (IOException e)
		{
			String msg = Messages.SchemaBuilder_IO_Error;
			SchemaInitializationException ie = new SchemaInitializationException(msg, e);

			throw ie;
		}

		// return results
		return result;
	}

	/**
	 * Process an &lt;attribute&gt;
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 * @param attributes
	 */
	public void startAttributeElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes)
	{
		String name = attributes.getValue(NAME_ATTRIBUTE);
		String usage = attributes.getValue(USAGE_ATTRIBUTE);

		this._currentElement.addAttribute(name, usage);
	}

	/**
	 * Process a &lt;child-element&gt;
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 * @param attributes
	 */
	public void startChildElementElement(String namespaceURI, String localName, String qualifiedName,
			Attributes attributes)
	{
		// get target element's name
		String elementName = attributes.getValue(NAME_ATTRIBUTE);

		// create a new SchemaElement for our target element
		ISchemaElement element = this._newSchema.createElement(elementName);

		this._currentElement.addTransition(element);
	}

	/**
	 * startDocument handler
	 */
	public void startDocument()
	{
		super.startDocument();

		// reset the stack
		this._elementStack.clear();

		// set the root element as our current element
		this._currentElement = this._newSchema.getRootElement();
	}

	/**
	 * Start processing a element element
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 * @param attributes
	 * @throws SAXException
	 */
	public void startElementElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes)
			throws SAXException
	{
		// get target element's name and type
		String elementName = attributes.getValue(NAME_ATTRIBUTE);
		String elementType = attributes.getValue(TYPE_ATTRIBUTE);
		String hasText = attributes.getValue(HAS_TEXT_ATTRIBUTE);
		String allowFreeformMarkup = attributes.getValue(ALLOW_FREEFORM_MARKUP_ATTRIBUTE);

		// create a new SchemaElement for our target element
		ISchemaElement element = this._newSchema.createElement(elementName);

		// tag as root node, if needed
		if (SCHEMA_1_0_NAMESPACE.equals(namespaceURI))
		{
			if (elementType != null && elementType.equals("root")) //$NON-NLS-1$
			{
				// add new target element as a transition from the current element
				this._currentElement.addTransition(element);
			}
		}
		else
		{
			// assuming 1.1

			// add new target element as a transition from the current element
			this._currentElement.addTransition(element);
		}

		// set hasText, if defined
		if (hasText != null && hasText.length() > 0)
		{
			String lowerHasText = hasText.toLowerCase();
			boolean hasTextValue = (lowerHasText.equals("true") || lowerHasText.equals("yes")); //$NON-NLS-1$ //$NON-NLS-2$

			element.setHasText(hasTextValue);
		}

		// set allowFreeformMarkup, if defined
		if (allowFreeformMarkup != null && allowFreeformMarkup.length() > 0)
		{
			String lowerText = allowFreeformMarkup.toLowerCase();
			boolean allowFreeformMarkupValue = (lowerText.equals("true") || lowerText.equals("yes")); //$NON-NLS-1$ //$NON-NLS-2$

			element.setAllowFreeformMarkup(allowFreeformMarkupValue);
		}

		// save current element on the stack
		this._elementStack.push(this._currentElement);

		// set our new element as the new current element
		this._currentElement = element;
	}

	/**
	 * Start processing a element-set element
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 * @param attributes
	 * @throws SAXException
	 */
	public void startElementSetElement(String namespaceURI, String localName, String qualifiedName,
			Attributes attributes)
	{
		// create a new SchemaElement for our target element
		ISchemaElement set = this._newSchema.createElement(localName, false);

		// get id
		String id = attributes.getValue("id"); //$NON-NLS-1$

		// save set name for later
		this._currentSetId = id;

		// save current element on the stack
		this._elementStack.push(this._currentElement);

		// set our new element as the new current element
		this._currentElement = set;
	}

	/**
	 * Start processing a schema element
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 * @param attributes
	 * @throws SAXException
	 */
	public void startSchemaElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes)
			throws SAXException
	{
		if (SCHEMA_1_0_NAMESPACE.equals(namespaceURI))
		{
			this._schema = this._schema10;
		}
		else if (SCHEMA_1_1_NAMESPACE.equals(namespaceURI))
		{
			this._schema = this._schema11;
		}
		else
		{
			String message = Messages.SchemaBuilder_Unknown_Schema_Namespace + namespaceURI;

			throw new SAXException(message);
		}

		try
		{
			this._schema.reset();
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
			throw new SAXException(e); // $codepro.audit.disable exceptionUsage.exceptionCreation
		}
	}

	/**
	 * Start processing a schema element
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 * @param attributes
	 * @throws SAXException
	 */
	public void startUseElementSetElement(String namespaceURI, String localName, String qualifiedName,
			Attributes attributes)
	{
		String name = attributes.getValue(NAME_ATTRIBUTE);
		String id = name.substring(1);

		this.addSetToElement(id, this._currentElement);
	}

	/**
	 * addSetToElement
	 * 
	 * @param id
	 *            The name of the element to add
	 * @param element
	 *            The element to which the set children will be added
	 */
	private void addSetToElement(String id, ISchemaElement element)
	{
		if (this._sets.containsKey(id))
		{
			ISchemaElement set = this._sets.get(id);
			ISchemaElement[] children = set.getTransitionElements();

			if (children != null)
			{
				for (ISchemaElement child : children)
				{
					this._currentElement.addTransition(child);
				}
			}
		}
		else
		{
			throw new IllegalArgumentException(Messages.SchemaBuilder_Set_ID_Not_Defined + id);
		}
	}
}
