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

public class JSForOfNode extends JSNode
{
	private Symbol _leftParenthesis;
	private Symbol _of;
	private Symbol _rightParenthesis;

	/**
	 * JSForOFNode
	 * 
	 * @param children
	 */
	public JSForOfNode(Symbol leftParenthesis, JSNode initializer, Symbol of, JSNode expression,
			Symbol rightParenthesis, JSNode body)
	{
		super(IJSNodeTypes.FOR_OF, initializer, expression, body);

		this._leftParenthesis = leftParenthesis;
		this._of = of;
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
	public Symbol getOf()
	{
		return this._of;
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
