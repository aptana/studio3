package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

public class JSGetElementNode extends JSBinaryOperatorNode
{
	private Symbol _rightBracket;

	/**
	 * JSGetElementOperatorNode
	 * 
	 * @param left
	 * @param right
	 */
	public JSGetElementNode(JSNode left, Symbol leftBracket, JSNode right, Symbol rightBracket)
	{
		super(left, leftBracket, right);
		
		this._rightBracket = rightBracket;
		this.setType(JSNodeTypes.GET_ELEMENT);
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
		return this.getOperator();
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSBinaryOperatorNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();

		text.append(this.getLeftHandSide());
		text.append("[");
		text.append(this.getRightHandSide());
		text.append("]"); //$NON-NLS-1$ //$NON-NLS-2$

		this.appendSemicolon(text);

		return text.toString();
	}
}
