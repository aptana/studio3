/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import java.util.ArrayList;
import java.util.Stack;

/**
 * SchemaHandler
 */
public class SchemaHandler implements IContextHandler
{
	private Stack<Object> _stack;
	private Schema _schema;

	/**
	 * SchemaHandler
	 */
	public SchemaHandler()
	{
		this._stack = new Stack<Object>();
		this._schema = new Schema();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#addElement(java.lang.String, com.aptana.json.IState)
	 */
	public void addElement(String elementTypeName, IState elementType)
	{
		System.out.println("add element of type '" + elementTypeName + "' to list");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#createList(java.lang.String, com.aptana.json.IState)
	 */
	public void createList(String elementTypeName, IState elementType)
	{
		System.out.println("create list of '" + elementTypeName + "'");
		
		this._stack.push(new ArrayList<Object>());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#createType(java.lang.String, com.aptana.json.IState)
	 */
	public void createType(String typeName, IState type)
	{
		System.out.println("create type '" + typeName + "'");

		Object instance = null;

		if ("Schema".equals(typeName))
		{
			instance = this._schema.addType(typeName);
		}
		else if ("SchemaDocument".equals(typeName))
		{
			instance = this._schema.addType(typeName);
		}
		else if ("Type".equals(typeName))
		{
			instance = this._schema.addType(typeName);
		}
		else if ("Property".equals(typeName))
		{
			instance = this._schema.addType(typeName);
		}
		else if (typeName != null && typeName.startsWith("Array<"))
		{
			// do nothing as this will be handled by createList
		}

		if (instance == null)
		{
			// throw exception
		}

		this._stack.push(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#setProperty(java.lang.String, com.aptana.json.IState)
	 */
	public void setProperty(String propertyName, String propertyTypeName, IState propertyType)
	{
		System.out.println("set property '" + propertyName + "'");

		if (this._stack.isEmpty() == false)
		{
			/* Object value = */this._stack.pop();
			Object top = this._stack.peek();

			if (top instanceof SchemaObject)
			{
				((SchemaObject) top).addProperty(propertyName, propertyTypeName);
			}
		}
	}
}
