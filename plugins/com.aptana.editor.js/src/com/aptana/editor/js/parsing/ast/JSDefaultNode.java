package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSDefaultNode extends JSNode
{
	private Symbol _colon;
	
	/**
	 * JSDefaultNode
	 * 
	 * @param start
	 * @param end
	 */
	public JSDefaultNode(Symbol colon, JSNode... children)
	{
		super(JSNodeTypes.DEFAULT, children);
		
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
}
