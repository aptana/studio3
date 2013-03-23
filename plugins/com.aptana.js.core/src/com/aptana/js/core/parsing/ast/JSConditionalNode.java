/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class JSConditionalNode extends JSNode
{
	private Symbol _questionMark;
	private Symbol _colon;

	/**
	 * JSConditionalNode
	 * 
	 * @param children
	 */
	public JSConditionalNode(JSNode test, Symbol questionMark, JSNode trueCase, Symbol colon, JSNode falseCase)
	{
		super(IJSNodeTypes.CONDITIONAL, test, trueCase, falseCase);

		this._questionMark = questionMark;
		this._colon = colon;
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
