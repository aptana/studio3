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
	private Stack<IState> _typeStack;
	private IState _currentType;
	private Stack<Integer> _topStack;
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
	 * @see com.aptana.json.IContextAction#createList(java.lang.String, com.aptana.json.IState)
	 */
	public void createList(String elementTypeName, IState elementType)
	{
		if (this._handler != null)
		{
			this._handler.createList(elementTypeName, elementType);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IContextAction#createType(java.lang.String, com.aptana.json.IState)
	 */
	public void createType(String typeName, IState type)
	{
		if (this._handler != null)
		{
			this._handler.createType(typeName, type);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#getCurrentType()
	 */
	public IState getCurrentType()
	{
		return this._currentType;
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
	 * getStackTop
	 * 
	 * @return
	 */
	public int getStackTop()
	{
		int result = 0;

		if (this._topStack != null && this._topStack.isEmpty() == false)
		{
			result = this._topStack.peek();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#popType()
	 */
	public void popType()
	{
		// NOTE: we leave the current type intact when we determine we can't pop. This is to preserve the current type
		// for array elements
		if (this._typeStack != null && this._typeStack.size() > this.getStackTop())
		{
			this._currentType = this._typeStack.pop();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#pushType(java.lang.String, com.aptana.json.IState)
	 */
	public void pushType(IState type)
	{
		if (this._currentType != null)
		{
			if (this._typeStack == null)
			{
				this._typeStack = new Stack<IState>();
			}

			this._typeStack.push(this._currentType);
		}

		this._currentType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#reset()
	 */
	public void reset()
	{
		this._typeStack = null;
		this._currentType = null;
		this._topStack = null;
	}

	/**
	 * restoreTop
	 */
	public void restoreTop()
	{
		if (this._topStack != null)
		{
			this._topStack.pop();
		}
	}

	/**
	 * saveTop
	 */
	public void saveTop()
	{
		if (this._topStack == null)
		{
			this._topStack = new Stack<Integer>();
		}

		int top = (this._typeStack != null) ? this._typeStack.size() : 0;

		this._topStack.push(top);
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
	public void setProperty(String propertyName, IState propertyType)
	{
		if (this._handler != null)
		{
			this._handler.setProperty(propertyName, propertyType);
		}
	}
}
