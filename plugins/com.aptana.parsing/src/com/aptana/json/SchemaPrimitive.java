/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * SchemaPrimitive
 */
public class SchemaPrimitive implements IState
{
	private String _text;

	/**
	 * SchemaPrimitive
	 * 
	 * @param text
	 */
	public SchemaPrimitive(String text)
	{
		this._text = text;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.State#enter()
	 */
	public void enter()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.State#exit()
	 */
	public void exit()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IState#isValidTransition(com.aptana.json.EventType, java.lang.Object)
	 */
	public boolean isValidTransition(EventType event, Object value)
	{
		boolean result = false;

		if (event == EventType.PRIMITIVE && value instanceof String)
		{
			result = value.toString().equals(this._text);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IState#transition(com.aptana.json.ISchemaContext, com.aptana.json.EventType,
	 * java.lang.Object)
	 */
	public void transition(ISchemaContext context, EventType event, Object value)
	{
		if (this.isValidTransition(event, value) == false)
		{
			throw new IllegalStateException();
		}
	}
}
