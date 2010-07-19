package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class JSIfNode extends JSNode
{
	private Symbol _leftParenthesis;
	private Symbol _rightParenthesis;

	/**
	 * JSIfNode
	 * 
	 * @param children
	 */
	public JSIfNode(Symbol leftParenthesis, JSNode condition, Symbol rightParenthesis, JSNode trueBlock, JSNode falseBlock)
	{
		super(JSNodeTypes.IF, condition, trueBlock, falseBlock);
		
		this._leftParenthesis = leftParenthesis;
		this._rightParenthesis = rightParenthesis;
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
	 * getCondition
	 * 
	 * @return
	 */
	public IParseNode getCondition()
	{
		return this.getChild(0);
	}

	/**
	 * getFalseBlock
	 * 
	 * @return
	 */
	public IParseNode getFalseBlock()
	{
		return this.getChild(2);
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

	/**
	 * getTrueBlock
	 * 
	 * @return
	 */
	public IParseNode getTrueBlock()
	{
		return this.getChild(1);
	}
}
