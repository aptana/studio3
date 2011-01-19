/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

public class CSSExpressionNode extends CSSNode
{
	/**
	 * CSSExpressionNode
	 * 
	 * @param start
	 * @param end
	 */
	public CSSExpressionNode(int start, int end)
	{
		super(CSSNodeTypes.EXPRESSION, start, end);
	}

	/**
	 * CSSExpressionNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 */
	public CSSExpressionNode(short type, int start, int end)
	{
		super(type, start, end);
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
