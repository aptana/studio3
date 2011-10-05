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
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.IState#getTypeName()
	 */
	public String getTypeName()
	{
		return "String"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.json.SchemaPrimitive#validValue(java.lang.Object)
	 */
	@Override
	protected boolean validValue(Object value)
	{
		return value instanceof String;
	}
}
