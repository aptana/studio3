package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSArrayNode extends JSNode
{
	private Symbol _leftBracket;
	private Symbol _rightBracket;

	/**
	 * JSArrayNode
	 * 
	 * @param elements
	 */
	public JSArrayNode(Symbol leftBracket, Symbol rightBracket, JSNode... elements)
	{
		super(JSNodeTypes.ARRAY_LITERAL, elements);

		this._leftBracket = leftBracket;
		this._rightBracket = rightBracket;
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
	 * getLeftBracket
	 * 
	 * @return
	 */
	public Symbol getLeftBracket()
	{
		return this._leftBracket;
	}

	/**
	 * getRightBracket
	 * 
	 * @return
	 */
	public Symbol getRightBracket()
	{
		return this._rightBracket;
	}
}
