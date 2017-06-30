/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

public class JSGroupNode extends JSPreUnaryOperatorNode
{
	private Symbol _leftParenthesis;
	private Symbol _rightParenthesis;

	/**
	 * JSGroupNode
	 * 
	 * @param expression
	 */
	public JSGroupNode(Symbol leftParenthesis, JSNode expression, Symbol rightParenthesis)
	{
		this(leftParenthesis, rightParenthesis);
		setChildren(new JSNode[] { expression });
	}

	/**
	 * Used by ANTLR AST
	 * 
	 * @param leftParenthesis
	 * @param rightParenthesis
	 */
	public JSGroupNode(Symbol leftParenthesis, Symbol rightParenthesis)
	{
		super(IJSNodeTypes.GROUP);

		this._leftParenthesis = leftParenthesis;
		this._rightParenthesis = rightParenthesis;
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
	 * getLeftParenthesis
	 * 
	 * @return
	 */
	public Symbol getLeftParenthesis()
	{
		return this._leftParenthesis;
	}

	/**
	 * getRightParenthesis
	 * 
	 * @return
	 */
	public Symbol getRightParenthesis()
	{
		return this._rightParenthesis;
	}
}
