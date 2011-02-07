/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * SchemaType
 */
public class SchemaType
{
	private String _name;
	private SchemaValue _value;

	/**
	 * SchemaType
	 * 
	 * @param name
	 */
	public SchemaType(String name, SchemaValue value)
	{
		this._name = name;
		this._value = value;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}
	
	/**
	 * getValue
	 * 
	 * @return
	 */
	public SchemaValue getValue()
	{
		return this._value;
	}
}
