package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

public class JSInitializerNode extends JSNode
{
	private Symbol _equals;

	public JSInitializerNode(Symbol equals, JSNode expression)
	{
		super(IJSNodeTypes.INITIALIZER, expression);
		this._equals = equals;
	}

	public Symbol getEquals()
	{
		return this._equals;
	}

	public JSNode getExpression()
	{
		return (JSNode) getFirstChild();
	}
}
