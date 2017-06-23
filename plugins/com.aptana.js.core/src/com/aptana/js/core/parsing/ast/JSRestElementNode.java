package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

/**
 * This is the "rest" arguments given to an arrow or normal function declaration; or in an array binding This holds the
 * ... symbol and the identifier
 * 
 * @author cwilliams
 */
public class JSRestElementNode extends JSNode
{
	private Symbol _ellipsis;

	public JSRestElementNode(Symbol ellipsis, JSIdentifierNode ident)
	{
		super(IJSNodeTypes.REST_ELEMENT, ident);
		this._ellipsis = ellipsis;
	}

	public Symbol getEllipsis()
	{
		return this._ellipsis;
	}

}
