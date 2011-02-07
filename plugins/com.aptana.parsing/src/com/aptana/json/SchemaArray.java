/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

/**
 * SchemaArray
 */
public class SchemaArray extends SchemaValue
{
	private SchemaType _elementType;

	/**
	 * SchemaArray
	 * 
	 * @param elementType
	 */
	public SchemaArray(SchemaType elementType)
	{
		this._elementType = elementType;
	}

	/**
	 * getElementType
	 * 
	 * @return
	 */
	public SchemaType getElementType()
	{
		return this._elementType;
	}
}
