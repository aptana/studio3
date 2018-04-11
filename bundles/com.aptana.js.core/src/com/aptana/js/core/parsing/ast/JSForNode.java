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

public class JSForNode extends JSNode
{
	private Symbol _leftParenthesis;
	private Symbol _semicolon1;
	private Symbol _semicolon2;
	private Symbol _rightParenthesis;

	/**
	 * JSForNode
	 * 
	 * @param children
	 */
	public JSForNode(Symbol leftParenthesis, JSNode initializer, Symbol semicolon1, JSNode condition,
			Symbol semicolon2, JSNode advance, Symbol rightParenthesis, JSNode body)
	{
		super(IJSNodeTypes.FOR, initializer, condition, advance, body);

		this._leftParenthesis = leftParenthesis;
		this._semicolon1 = semicolon1;
		this._semicolon2 = semicolon2;
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
	 * getAdvance
	 * 
	 * @return
	 */
	public IParseNode getAdvance()
	{
		return this.getChild(2);
	}

	/**
	 * getBody
	 * 
	 * @return
	 */
	public IParseNode getBody()
	{
		return this.getChild(3);
	}

	/**
	 * getCondition
	 * 
	 * @return
	 */
	public IParseNode getCondition()
	{
		return this.getChild(1);
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

	/**
	 * getSemicolon1
	 * 
	 * @return
	 */
	public Symbol getSemicolon1()
	{
		return this._semicolon1;
	}

	/**
	 * getSemicolon2
	 * 
	 * @return
	 */
	public Symbol getSemicolon2()
	{
		return this._semicolon2;
	}
}
