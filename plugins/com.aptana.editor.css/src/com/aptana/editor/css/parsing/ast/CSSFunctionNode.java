/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

public class CSSFunctionNode extends CSSExpressionNode
{

	public CSSFunctionNode(CSSExpressionNode expression, int start, int end)
	{
		super(CSSNodeTypes.FUNCTION, start, end);
		setChildren(new CSSNode[] { expression });
	}

	public CSSExpressionNode getExpression()
	{
		return (CSSExpressionNode) getChild(0);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSFunctionNode))
		{
			return false;
		}
		CSSFunctionNode other = (CSSFunctionNode) obj;
		return toString().equals(other.toString());
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + toString().hashCode();
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append("(").append(getExpression()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
		return text.toString();
	}
}
