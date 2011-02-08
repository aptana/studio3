/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * SchemaString
 */
public class SchemaString implements State
{
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
	 * @see com.aptana.json.State#transition(com.aptana.json.EventType, com.aptana.json.Context)
	 */
	public void transition(ISchemaContext context, EventType event, Object value)
	{
		if (event == EventType.PRIMITIVE && value instanceof String)
		{
			// OK
		}
		else
		{
			throw new IllegalStateException();
		}
	}
}
