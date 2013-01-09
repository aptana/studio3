/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

public class CSSAttributeSelectorNode extends CSSNode
{
	private String fAttributeText;

	/**
	 * ":" + function expression
	 * 
	 * @param function
	 *            the function expression
	 */
	public CSSAttributeSelectorNode(CSSExpressionNode function)
	{
		setChildren(new CSSNode[] { function });
	}

	/**
	 * CSSAttributeSelectorNode
	 * 
	 * @param text
	 */
	public CSSAttributeSelectorNode(String text)
	{
		fAttributeText = text;
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.ATTRIBUTE_SELECTOR;
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
		if (!super.equals(obj) || !(obj instanceof CSSAttributeSelectorNode))
		{
			return false;
		}

		CSSAttributeSelectorNode other = (CSSAttributeSelectorNode) obj;

		return toString().equals(other.toString());
	}

	/**
	 * getFunction
	 * 
	 * @return
	 */
	public CSSExpressionNode getFunction()
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
		if (fAttributeText == null)
		{
			return ":" + getFunction(); //$NON-NLS-1$
		}

		return fAttributeText;
	}
}
