/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

import beaver.Symbol;

public class JSForInNode extends JSNode
{
	private Symbol _leftParenthesis;
	private Symbol _in;
	private Symbol _rightParenthesis;

	/**
	 * JSForInNode
	 * 
	 * @param children
	 */
	public JSForInNode(Symbol leftParenthesis, JSNode initializer, Symbol in, JSNode expression,
			Symbol rightParenthesis, JSNode body)
	{
		this(leftParenthesis, in, rightParenthesis);
		setChildren(new JSNode[] { initializer, expression, body });
	}

	/**
	 * Used by ANTLR AST
	 * 
	 * @param leftParenthesis
	 * @param in
	 * @param rightParenthesis
	 */
	public JSForInNode(Symbol leftParenthesis, Symbol in, Symbol rightParenthesis)
	{
		super(IJSNodeTypes.FOR_IN);

		this._leftParenthesis = leftParenthesis;
		this._in = in;
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
	 * getBody
	 * 
	 * @return
	 */
	public IParseNode getBody()
	{
		return this.getChild(2);
	}

	/**
	 * getExpression
	 * 
	 * @return
	 */
	public IParseNode getExpression()
	{
		return this.getChild(1);
	}

	/**
	 * getIn
	 * 
	 * @return
	 */
	public Symbol getIn()
	{
		return this._in;
	}

	/**
	 * getInitialization
	 * 
	 * @return
	 */
	public IParseNode getInitializer()
	{
		return this.getChild(0);
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
