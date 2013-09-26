/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class HTMLSpecialNode extends HTMLElementNode
{

	public HTMLSpecialNode(Symbol tag, IParseNode[] children, int start, int end)
	{
		super(tag, start, end);
		setChildren(children);
		setType(IHTMLNodeTypes.SPECIAL);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof HTMLSpecialNode))
		{
			return false;
		}
		return super.equals(obj);
	}
}
