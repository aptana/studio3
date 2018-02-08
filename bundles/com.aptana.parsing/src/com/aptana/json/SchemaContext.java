/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import java.util.Stack;

/**
 * Context
 */
public class SchemaContext implements ISchemaContext
{
	private Stack<IState> _typeStack; // $codepro.audit.disable declareAsInterface
	private IContextHandler _handler;

	/**
	 * Context
	 */
	public SchemaContext()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#addElement(java.lang.String, com.aptana.json.IState)
	 */
	public void addElement(String elementTypeName, IState elementType)
	{
		if (this._handler != null)
		{
			this._handler.addElement(elementTypeName, elementType);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#createType(java.lang.String, com.aptana.json.IState)
	 */
	public void createType(String typeName, IState type, Object value)
	{
		if (this._handler != null)
		{
			this._handler.createType(typeName, type, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#getCurrentType()
	 */
	public IState getCurrentType()
	{
		IState result = null;

		if (this.hasTypes())
		{
			result = this._typeStack.peek();
		}

		return result;
	}

	/**
	 * getHandler
	 * 
	 * @return
	 */
	public IContextHandler getHandler()
	{
		return this._handler;
	}

	/**
	 * hasTypes
	 * 
	 * @return
	 */
	public boolean hasTypes()
	{
		return this._typeStack != null && !this._typeStack.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#popType()
	 */
	public void popType()
	{
		// NOTE: we leave the current type intact when we determine we can't pop. This is to preserve the current type
		// for array elements
		if (this._typeStack != null)
		{
			if (!this._typeStack.isEmpty())
			{
				this._typeStack.pop();
			}
			else
			{
				throw new IllegalArgumentException(Messages.SchemaContext_Popped_Empty_Stack);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#pushType(java.lang.String, com.aptana.json.IState)
	 */
	public void pushType(String typeName, IState type)
	{
		if (this._typeStack == null)
		{
			this._typeStack = new Stack<IState>();
		}

		this._typeStack.push(type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#reset()
	 */
	public void reset()
	{
		this._typeStack = null;
	}

	/**
	 * setHandler
	 * 
	 * @param handler
	 */
	public void setHandler(IContextHandler handler)
	{
		this._handler = handler;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#setProperty(java.lang.String, com.aptana.json.IState)
	 */
	public void setProperty(String propertyName, String propertyTypeName, IState propertyType)
	{
		if (this._handler != null)
		{
			this._handler.setProperty(propertyName, propertyTypeName, propertyType);
		}
	}
}
