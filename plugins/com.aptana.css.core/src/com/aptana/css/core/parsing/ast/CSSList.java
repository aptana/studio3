/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

public class CSSList extends CSSNode
{
	/**
	 * CSSList
	 */
	public CSSList()
	{
	}

	/**
	 * CSSList
	 * 
	 * @param start
	 * @param end
	 */
	public CSSList(int start, int end)
	{
		super(start, end);
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.LIST;
	}
}
