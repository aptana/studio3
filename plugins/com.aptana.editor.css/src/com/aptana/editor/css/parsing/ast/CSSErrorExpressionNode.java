/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

public class CSSErrorExpressionNode extends CSSExpressionNode
{
	/**
	 * CSSErrorExpressionNode
	 * 
	 * @param start
	 * @param end
	 */
	public CSSErrorExpressionNode()
	{
		super();
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.EXPRESSION;
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
}
