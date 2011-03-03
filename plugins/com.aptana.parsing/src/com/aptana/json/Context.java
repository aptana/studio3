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
public class Context implements ISchemaContext
{
	private Stack<IState> _typeStack;
	private IState _currentType;
	private Stack<Integer> _topStack;

	/**
	 * Context
	 */
	public Context()
	{
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
	 * @see com.aptana.json.ISchemaContext#pushType(com.aptana.json.State)
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
}
