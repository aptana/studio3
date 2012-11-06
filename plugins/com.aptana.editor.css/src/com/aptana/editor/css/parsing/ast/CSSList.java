/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

/**
 * CSSList
 */
public class CSSList extends CSSNode
{
	/**
	 * CSSList
	 * 
	 * @param type
	 */
	public CSSList()
	{
		super();
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
