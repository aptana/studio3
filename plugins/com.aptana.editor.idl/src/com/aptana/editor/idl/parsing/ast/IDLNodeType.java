/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.idl.parsing.ast;

import java.util.EnumSet;

public enum IDLNodeType
{
	EMPTY,
	INTERFACE;

	private short _index;

	/**
	 * static initializer
	 */
	static
	{
		short index = 0;

		for (IDLNodeType type : EnumSet.allOf(IDLNodeType.class))
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
