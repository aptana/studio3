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
public class Schema implements IState
{
	private IState _schemaType;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.SchemaState#enter()
	 */
	public void enter()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.SchemaState#exit()
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
		return event == EventType.START_PARSE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.SchemaState#transition(com.aptana.json.EventType)
	 */
	public void transition(ISchemaContext context, EventType event, Object value)
	{
		if (event != EventType.START_PARSE)
		{
			throw new IllegalStateException("Unsupported event type: " + event.name());
		}

		context.pushType(this._schemaType);
	}
}
