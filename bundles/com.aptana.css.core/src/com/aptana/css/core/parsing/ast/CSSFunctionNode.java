/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

public class CSSFunctionNode extends CSSExpressionNode
{
	private String fName;

	/**
	 * CSSFunctionNode
	 * 
	 * @param name
	 * @param expression
	 */
	public CSSFunctionNode(String name, CSSExpressionNode expression)
	{
		fName = name;

		if (expression != null)
		{
			setChildren(new CSSNode[] { expression });
		}
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.FUNCTION;
	}

	@Override
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
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

	/**
	 * getExpression
	 * 
	 * @return
	 */
	public CSSExpressionNode getExpression()
	{
		if (getChildCount() <= 0) {
			return null;
		}
		return (CSSExpressionNode) getChild(0);
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return fName;
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
		CSSExpressionNode expr = getExpression();

		text.append(getName());

		if (expr != null)
		{
			text.append('(').append(getExpression()).append(')');
		}
		else
		{
			text.append("()"); //$NON-NLS-1$
		}

		return text.toString();
	}
}
