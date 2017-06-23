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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}
	
	public Symbol getEllipsis()
	{
		return this._ellipsis;
	}

}
