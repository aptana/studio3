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
public class Context
{
	private Stack<Type> _typeStack;
	private Type _currentType;

	/**
	 * Context
	 */
	public Context()
	{
	}

	/**
	 * popType
	 */
	public void popType()
	{
		this._currentType = this._typeStack.pop();
	}

	/**
	 * pushType
	 * 
	 * @param type
	 */
	public void pushType(Type type)
	{
		if (this._currentType != null)
		{
			if (this._typeStack == null)
			{
				this._typeStack = new Stack<Type>();
			}
		}

		this._currentType = type;
	}
}
