/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

public class JSGetElementNode extends JSBinaryOperatorNode
{
	private Symbol _rightBracket;

	/**
	 * JSGetElementOperatorNode
	 * 
	 * @param left
	 * @param right
	 */
	public JSGetElementNode(JSNode left, Symbol leftBracket, JSNode right, Symbol rightBracket)
	{
		super(left, leftBracket, right);

		this._rightBracket = rightBracket;
		this.setNodeType(IJSNodeTypes.GET_ELEMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}

	/**
	 * getLeftBracket
	 * 
	 * @return
	 */
	public Symbol getLeftBracket()
	{
		return this.getOperator();
	}

	/**
	 * getRightBracket
	 * 
	 * @return
	 */
	public Symbol getRightBracket()
	{
		return this._rightBracket;
	}
}
