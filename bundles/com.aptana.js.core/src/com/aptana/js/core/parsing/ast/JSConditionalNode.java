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

public class JSConditionalNode extends JSNode
{
	private Symbol _questionMark;
	private Symbol _colon;

	/**
	 * USed by ANTLR AST
	 * @param start
	 * @param end
	 * @param questionMark
	 * @param colon
	 */
	public JSConditionalNode(int start, int end, Symbol questionMark, Symbol colon)
	{
		super(IJSNodeTypes.CONDITIONAL);

		this._questionMark = questionMark;
		this._colon = colon;
		this.setLocation(start, end);
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
	 * getColon
	 * 
	 * @return
	 */
	public Symbol getColon()
	{
		return this._colon;
	}

	/**
	 * getFalseExpression
	 * 
	 * @return
	 */
	public IParseNode getFalseExpression()
	{
		return this.getChild(2);
	}

	/**
	 * getQuestionMark
	 * 
	 * @return
	 */
	public Symbol getQuestionMark()
	{
		return this._questionMark;
	}

	/**
	 * getTestExpression
	 * 
	 * @return
	 */
	public IParseNode getTestExpression()
	{
		return this.getChild(0);
	}

	/**
	 * getTrueExpression
	 * 
	 * @return
	 */
	public IParseNode getTrueExpression()
	{
		return this.getChild(1);
	}
}
