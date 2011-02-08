/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * SchemaProperty
 */
public class Property
{
	private String _name;
	private IState _type;

	/**
	 * SchemaProperty
	 * 
	 * @param name
	 * @param type
	 */
	public Property(String name, IState type)
	{
		this._name = name;
		this._type = type;
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
	 * getType
	 * 
	 * @return
	 */
	public IState getType()
	{
		return this._type;
	}
}
