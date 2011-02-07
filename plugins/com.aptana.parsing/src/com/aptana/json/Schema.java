/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * Schema
 */
public class Schema implements State
{
	private Type _schemaType;
	
	/* (non-Javadoc)
	 * @see com.aptana.json.SchemaState#enter()
	 */
	public void enter()
	{
	}

	/* (non-Javadoc)
	 * @see com.aptana.json.SchemaState#transition(com.aptana.json.EventType)
	 */
	public void transition(Context context, EventType event, Object value)
	{
		if (event == EventType.START_PARSE)
		{
			context.pushType(this._schemaType);
		}
		else
		{
			throw new IllegalStateException();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.json.SchemaState#exit()
	 */
	public void exit()
	{
	}
}
