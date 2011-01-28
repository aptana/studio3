/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

public class CSSFunctionNode extends CSSExpressionNode
{
	private String fName;

	/**
	 * CSSFunctionNode
	 * 
	 * @param expression
	 * @param start
	 * @param end
	 */
	public CSSFunctionNode(String name, CSSExpressionNode expression)
	{
		super(CSSNodeTypes.FUNCTION);

		fName = name;

		this.setChildren(new CSSNode[] { expression });
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#accept(com.aptana.editor.css.parsing.ast.CSSTreeWalker)
	 */
	@Override
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#equals(java.lang.Object)
	 */
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
		return (CSSExpressionNode) getChild(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + toString().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();

		text.append(fName);
		text.append("(").append(getExpression()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$

		return text.toString();
	}
}
