/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import java.util.ArrayList;
import java.util.List;
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
	@SuppressWarnings("unchecked")
	public void addElement(String elementTypeName, IState elementType)
	{
		System.out.println(Messages.SchemaHandler_0 + elementTypeName + Messages.SchemaHandler_1);

		Object item = this._stack.pop();
		Object list = this._stack.peek();

		if (list instanceof List<?>)
		{
			((List<Object>) list).add(item);
		}
		else
		{
			System.out.println(Messages.SchemaHandler_2 + item + Messages.SchemaHandler_3 + list);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextHandler#createType(java.lang.String, com.aptana.json.IState, java.lang.Object)
	 */
	public void createType(String typeName, IState type, Object value)
	{
		System.out.println(Messages.SchemaHandler_4 + typeName + Messages.SchemaHandler_5 + value);

		Object instance = null;

		if (type instanceof SchemaPrimitive)
		{
			instance = value;
		}
		else if ("Schema".equals(typeName)) //$NON-NLS-1$
		{
			// return the internal schema instance
			instance = this._schema;
		}
		else if (typeName.startsWith("Array<")) //$NON-NLS-1$
		{
			instance = new ArrayList<Object>();
		}
		else
		{
			// create a new container to hold type information
			instance = this._schema.createObject();
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
		System.out.println(Messages.SchemaHandler_8 + propertyName + Messages.SchemaHandler_9 + propertyTypeName);

		if (this._stack.isEmpty() == false)
		{
			Object value = this._stack.pop();
			//Object name = this._stack.pop();
			Object top = this._stack.peek();

			// TODO: verify value type and name type

			if (top instanceof IPropertyContainer)
			{
				IPropertyContainer container = (IPropertyContainer) top;

				container.setProperty(propertyName, propertyTypeName, value);
			}
			else
			{
				// TODO: warn?
			}
		}
	}
}
