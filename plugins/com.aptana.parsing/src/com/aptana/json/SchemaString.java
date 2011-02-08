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
public class SchemaString extends SchemaPrimitive
{
	public SchemaString()
	{
		super(null);
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.json.SchemaPrimitive#isValidTransition(com.aptana.json.EventType, java.lang.Object)
	 */
	@Override
	public boolean isValidTransition(EventType event, Object value)
	{
		return (event == EventType.PRIMITIVE && value instanceof String);
	}
}
