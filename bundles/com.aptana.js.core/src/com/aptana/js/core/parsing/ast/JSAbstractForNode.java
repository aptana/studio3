package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

/**
 * Abstract base class for the different for types.
 * 
 * @author cwilliams
 */
public abstract class JSAbstractForNode extends JSNode
{

	private Symbol _leftParenthesis;
	private Symbol _rightParenthesis;

	protected JSAbstractForNode(short type, int start, int end, Symbol leftParenthesis, Symbol rightParenthesis)
	{
		super(type);
		this._leftParenthesis = leftParenthesis;
		this._rightParenthesis = rightParenthesis;
		this.setLocation(start, end);
	}

	public void replaceInit(JSNode combinedVarDecls)
	{
		this.replaceChild(0, combinedVarDecls);
		// Fix the lParen position
//		int lParenOffset = combinedVarDecls.getStart() - 1;
//		Symbol newLParen = new Symbol(_leftParenthesis.getId(), lParenOffset, lParenOffset, _leftParenthesis.value);
//		this._leftParenthesis = newLParen;
	}

	/**
	 * getLeftParenthesis
	 * 
	 * @return
	 */
	public Symbol getLeftParenthesis()
	{
		return this._leftParenthesis;
	}

	/**
	 * getRightParenthesis
	 * 
	 * @return
	 */
	public Symbol getRightParenthesis()
	{
		return this._rightParenthesis;
	}
}
