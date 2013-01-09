/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

public class CSSTermListNode extends CSSExpressionNode
{
	private String fSeparator;

	/**
	 * CSSTermListNode
	 * 
	 * @param left
	 * @param right
	 */
	public CSSTermListNode(CSSExpressionNode left, CSSExpressionNode right)
	{
		this(left, right, null);
	}

	/**
	 * CSSTermListNode
	 * 
	 * @param left
	 * @param right
	 * @param separator
	 */
	public CSSTermListNode(CSSExpressionNode left, CSSExpressionNode right, String separator)
	{
		this.fSeparator = separator;

		setChildren(new CSSExpressionNode[] { left, right });
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.TERM_LIST;
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
		if (!super.equals(obj) || !(obj instanceof CSSTermListNode))
		{
			return false;
		}

		CSSTermListNode other = (CSSTermListNode) obj;

		return toString().equals(other.toString());
	}

	/**
	 * getLeftExpression
	 * 
	 * @return
	 */
	public CSSExpressionNode getLeftExpression()
	{
		return (CSSExpressionNode) getChild(0);
	}

	/**
	 * getRightExpression
	 * 
	 * @return
	 */
	public CSSExpressionNode getRightExpression()
	{
		return (CSSExpressionNode) getChild(1);
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

		text.append(getLeftExpression());

		if (fSeparator == null)
		{
			text.append(' ');
		}
		else
		{
			text.append(fSeparator);
		}

		text.append(getRightExpression());

		return text.toString();
	}
}
