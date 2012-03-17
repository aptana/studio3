/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.sax;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.aptana.core.util.SourcePrinter;

/**
 * @author Kevin Lindsey
 */
public class Schema
{
	private Map<String, ISchemaElement> _elementsByName;
	private SchemaElement _rootElement;
	private Stack<ISchemaElement> _elementStack; // $codepro.audit.disable declareAsInterface
	private ISchemaElement _currentElement;

	private boolean _allowFreeformMarkup;

	/**
	 * allowFreeformMarkup
	 * 
	 * @return
	 */
	public boolean allowFreeformMarkup()
	{
		return this._allowFreeformMarkup;
	}

	/**
	 * Get the SchemaElement that serves as the root of this schema state machine
	 * 
	 * @return This schema's root element (like #document)
	 */
	public SchemaElement getRootElement()
	{
		return this._rootElement;
	}

	/**
	 * Determine if the given element name exists in this schema
	 * 
	 * @param name
	 *            The name of the element to test
	 * @return Returns true if the given element name exists in this schema
	 */
	public boolean hasElement(String name)
	{
		return this._elementsByName.containsKey(name);
	}

	/**
	 * setAllowFreeformMarkup
	 * 
	 * @param value
	 */
	public void setAllowFreeformMarkup(boolean value)
	{
		this._allowFreeformMarkup = value;
	}

	/**
	 * Set the root element of this schema. If the element does not exist, it will be added automatically to this schema
	 * 
	 * @param name
	 *            The name of the element to set as the root element
	 */
	public void setRootElement(String name)
	{
		ISchemaElement target;

		if (this.hasElement(name))
		{
			target = this._elementsByName.get(name);
		}
		else
		{
			target = this.createElement(name);
		}

		this._rootElement.addTransition(target);
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of SchemaGraph
	 * 
	 * @param handler
	 *            The handler to be used for event callbacks
	 */
	public Schema()
	{
		this._elementsByName = new HashMap<String, ISchemaElement>();
		this._elementStack = new Stack<ISchemaElement>();
		this._rootElement = new SchemaElement(this, "#document"); //$NON-NLS-1$
	}

	/*
	 * Methods
	 */

	/**
	 * Create a new SchemaElement for the given name. If the element name has already been created, then return that
	 * previous instance
	 * 
	 * @param name
	 *            The name of the element to create
	 * @return Returns the SchemaElement for the given name
	 */
	public ISchemaElement createElement(String name)
	{
		return this.createElement(name, true);
	}

	/**
	 * createElement
	 * 
	 * @param name
	 *            The name of the element to create
	 * @param unique
	 *            If true then only one element will ever be created created for a given name
	 * @return Returns the SchemaElement for the given name
	 */
	public ISchemaElement createElement(String name, boolean unique)
	{
		ISchemaElement result = null;

		if (unique)
		{
			if (this.hasElement(name))
			{
				result = this._elementsByName.get(name);
			}
			else
			{
				result = new SchemaElement(this, name);

				this._elementsByName.put(name, result);
			}
		}
		else
		{
			result = new SchemaElement(this, name);
		}

		return result;
	}

	/**
	 * Try to move to a new element along a valid transition
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 * @param attributes
	 * @throws InvalidTransitionException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SAXException
	 */
	public void moveTo(String namespaceURI, String localName, String qualifiedName, Attributes attributes)
			throws InvalidTransitionException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SAXException
	{
		if (!this._currentElement.isValidTransition(localName))
		{
			Object[] messageArgs = new Object[] { localName, this._currentElement.getName() };
			String message = MessageFormat.format(Messages.Schema_Invalid_Child, messageArgs);
			SourcePrinter writer = new SourcePrinter();

			writer.println();
			writer.println(message);

			this.buildErrorMessage(writer, localName, attributes);

			throw new InvalidTransitionException(writer.toString());
		}

		// push current element onto stack
		this._elementStack.push(this._currentElement);

		// set new current element
		this._currentElement = this._currentElement.moveTo(localName);

		// validate attributes on the new element
		this._currentElement.validateAttributes(attributes);
	}

	/**
	 * buildErrorMessage
	 * 
	 * @param writer
	 * @param localName
	 * @param attributes
	 */
	public void buildErrorMessage(SourcePrinter writer, String localName, Attributes attributes)
	{
		writer.println().println(Messages.Schema_Element_Stack_Trace);

		for (int i = 0; i < Messages.Schema_Element_Stack_Trace.length(); i++)
		{
			writer.print('=');
		}

		writer.println();

		// print element stack
		for (int i = 1; i < this._elementStack.size(); i++)
		{
			ISchemaElement element = this._elementStack.get(i);

			writer.printlnWithIndent(element.toString()).increaseIndent();
		}

		// print parent
		if (!localName.equals(this._currentElement.getName()))
		{
			writer.printlnWithIndent(this._currentElement.toString()).increaseIndent();
		}

		// print element where error occurred
		writer.printWithIndent("<").print(localName); //$NON-NLS-1$
		for (int i = 0; i < attributes.getLength(); i++)
		{
			writer.print(' ').print(attributes.getLocalName(i)).print("=\"").print(attributes.getValue(i)).print('"'); //$NON-NLS-1$
		}
		writer.println("/>"); //$NON-NLS-1$

		// close parent
		if (!localName.equals(this._currentElement.getName()))
		{
			writer.decreaseIndent().printWithIndent("</").print(this._currentElement.getName()).println('>'); //$NON-NLS-1$
		}

		// close element stack
		for (int i = this._elementStack.size() - 1; i > 0; i--)
		{
			ISchemaElement element = this._elementStack.get(i);

			writer.decreaseIndent().printWithIndent("</").print(element.getName()).println('>'); //$NON-NLS-1$
		}
	}

	/**
	 * Exit the current element
	 * 
	 * @param namespaceURI
	 * @param localName
	 * @param qualifiedName
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SAXException
	 */
	public void exitElement(String namespaceURI, String localName, String qualifiedName)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SAXException
	{
		// get new current element
		this._currentElement = this._elementStack.pop();
	}

	/**
	 * Prepare this schema for a new parse
	 */
	public void reset()
	{
		// make sure we have a starting point
		if (this._rootElement == null)
		{
			throw new IllegalStateException(Messages.Schema_Missing_Root_Element);
		}

		// clear the stack
		this._elementStack.clear();

		// set the current SchemaElement
		this._currentElement = this._rootElement;
	}
}
