package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

public class JSSpreadElementNode extends JSNode
{
	private Symbol _ellipsis;

	public JSSpreadElementNode(int start, int end, Symbol ellipsis)
	{
		super(IJSNodeTypes.SPREAD_ELEMENT);
		this._ellipsis = ellipsis;
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

	public Symbol getEllipsis()
	{
		return this._ellipsis;
	}

}
