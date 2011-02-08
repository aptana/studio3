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
	private Stack<State> _typeStack;
	private State _currentType;

	/**
	 * Context
	 */
	public Context()
	{
	}

	/* (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#popType()
	 */
	public void popType()
	{
		if (this._typeStack != null && this._typeStack.empty() == false)
		{
			this._currentType = this._typeStack.pop();
		}
		else
		{
			this._currentType = null;
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#pushType(com.aptana.json.State)
	 */
	public void pushType(State type)
	{
		if (this._currentType != null)
		{
			if (this._typeStack == null)
			{
				this._typeStack = new Stack<State>();
			}
		}

		this._currentType = type;
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.json.ISchemaContext#reset()
	 */
	public void reset()
	{
		this._typeStack = null;
		this._currentType = null;
	}
}
