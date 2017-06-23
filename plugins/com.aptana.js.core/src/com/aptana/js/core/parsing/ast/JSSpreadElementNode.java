package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

public class JSSpreadElementNode extends JSNode
{
	private Symbol _ellipsis;

	public JSSpreadElementNode(Symbol ellipsis, JSNode expression)
	{
		super(IJSNodeTypes.SPREAD_ELEMENT, expression);
		this._ellipsis = ellipsis;
	}

	public Symbol getEllipsis()
	{
		return this._ellipsis;
	}

}
