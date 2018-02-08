/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json.core.parsing.ast;

import java.util.EnumSet;

public enum JSONNodeType
{
	EMPTY, ARRAY, OBJECT, ENTRY, TRUE, FALSE, NULL, STRING, NUMBER;

	private short _index;

	/**
	 * static initializer
	 */
	static
	{
		short index = 0;

		for (JSONNodeType type : EnumSet.allOf(JSONNodeType.class))
		{
			type._index = index++;
		}
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public short getIndex()
	{
		return this._index;
	}
}
