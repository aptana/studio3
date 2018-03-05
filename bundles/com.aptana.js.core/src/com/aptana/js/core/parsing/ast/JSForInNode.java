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

public class JSForInNode extends JSAbstractForNode
{
	private Symbol _in;

	/**
	 * Used by ANTLR AST
	 * 
	 * @param start
	 * @param end
	 * @param leftParenthesis
	 * @param in
	 * @param rightParenthesis
	 */
	public JSForInNode(int start, int end, Symbol leftParenthesis, Symbol in, Symbol rightParenthesis)
	{
		super(IJSNodeTypes.FOR_IN, start, end, leftParenthesis, rightParenthesis);
		this._in = in;
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
}
