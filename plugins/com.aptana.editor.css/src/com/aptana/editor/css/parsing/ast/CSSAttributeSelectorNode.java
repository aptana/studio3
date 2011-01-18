/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

public class CSSAttributeSelectorNode extends CSSNode
{

	private String fAttributeText;

	public CSSAttributeSelectorNode(String text, int start, int end)
	{
		super(CSSNodeTypes.ATTRIBUTE_SELECTOR, start, end);
		fAttributeText = text;
	}

	/**
	 * ":" + function expression
	 * 
	 * @param function
	 *            the function expression
	 */
	public CSSAttributeSelectorNode(CSSExpressionNode function, int start)
	{
		super(CSSNodeTypes.ATTRIBUTE_SELECTOR, start, function.getEnd());
		setChildren(new CSSNode[] { function });
	}

	public CSSExpressionNode getFunction()
	{
		return (CSSExpressionNode) getChild(0);
	}

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

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + toString().hashCode();
	}

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
