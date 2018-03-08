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

public class JSCatchNode extends JSNode
{

	private Symbol _leftParenthesis;
	private Symbol _rightParenthesis;

	public JSCatchNode(int start, int end, Symbol leftParenthesis, Symbol rightParenthesis)
	{
		super(IJSNodeTypes.CATCH);
		this.setLocation(start, end);
		this._leftParenthesis = leftParenthesis;
		this._rightParenthesis = rightParenthesis;
	}

	public Symbol getLeftParenthesis()
	{
		return this._leftParenthesis;
	}

	public Symbol getRightParenthesis()
	{
		return this._rightParenthesis;
	}

	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}

	public IParseNode getBody()
	{
		return this.getChild(1);
	}

	public IParseNode getIdentifier()
	{
		return this.getChild(0);
	}
}
