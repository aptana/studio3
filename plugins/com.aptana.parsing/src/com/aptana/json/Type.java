/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import com.aptana.core.util.StringUtil;

/**
 * SchemaType
 */
public class Type
{
	private String _name;

	/**
	 * Type
	 */
	public Type()
	{
		this(StringUtil.EMPTY);
	}

	/**
	 * Type
	 * 
	 * @param name
	 */
	public Type(String name)
	{
		this._name = name;
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
}
