package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSObjectNode extends JSNode
{
	private Symbol _leftBrace;
	private Symbol _rightBrace;

	/**
	 * JSObjectNode
	 * 
	 * @param leftBrace
	 * @param properties
	 * @param rightBrace
	 */
	public JSObjectNode(Symbol leftBrace, Symbol rightBrace, JSNode... properties)
	{
		super(JSNodeTypes.OBJECT_LITERAL, properties);

		this._leftBrace = leftBrace;
		this._rightBrace = rightBrace;
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
	 * getLeftBrace
	 * 
	 * @return
	 */
	public Symbol getLeftBrace()
	{
		return this._leftBrace;
	}
	
	/**
	 * getRightBrace
	 * 
	 * @return
	 */
	public Symbol getRightBrace()
	{
		return this._rightBrace;
	}
}
