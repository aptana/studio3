/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.dtd.core.parsing.ast;

import java.util.EnumSet;

public enum DTDNodeType
{
	ELEMENT_DECLARATION,
	ELEMENT,
	EMPTY,
	ANY,
	PCDATA,
	ZERO_OR_MORE,
	ONE_OR_MORE,
	OPTIONAL,
	AND_EXPRESSION,
	OR_EXPRESSION,

	ATTRIBUTE_LIST_DECLARATION,
	ATTRIBUTE,
	TYPE,
	NOTATION,
	ENUMERATION,

	P_ENTITY_DECLARATION,
	G_ENTITY_DECLARATION,
	NDATA_DECLARATION,

	NOTATION_DECLARATION,
	PROCESSING_INSTRUCTION,

	INCLUDE_SECTION,
	IGNORE_SECTION;

	private short _index;

	/**
	 * static initializer
	 */
	static
	{
		short index = 0;

		for (DTDNodeType type : EnumSet.allOf(DTDNodeType.class))
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
