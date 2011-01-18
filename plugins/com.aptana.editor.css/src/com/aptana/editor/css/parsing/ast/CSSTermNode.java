/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import beaver.Symbol;

public class CSSTermNode extends CSSExpressionNode
{

	private String fTerm;

	public CSSTermNode(Symbol term)
	{
		super(CSSNodeTypes.TERM, term.getStart(), term.getEnd());
		fTerm = term.value.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSTermNode))
		{
			return false;
		}
		CSSTermNode other = (CSSTermNode) obj;
		return fTerm.equals(other.fTerm);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + fTerm.hashCode();
	}

	@Override
	public String toString()
	{
		return fTerm;
	}
}
