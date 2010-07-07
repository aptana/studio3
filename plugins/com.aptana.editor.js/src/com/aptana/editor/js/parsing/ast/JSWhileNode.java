package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class JSWhileNode extends JSNode
{
	private Symbol _leftParenthesis;
	private Symbol _rightParenthesis;

	/**
	 * JSWhileNode
	 * 
	 * @param children
	 */
	public JSWhileNode(Symbol leftParenthesis, JSNode condition, Symbol rightParenthesis, JSNode body)
	{
		super(JSNodeTypes.WHILE, condition, body);
		
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
	 * getBody
	 * 
	 * @return
	 */
	public IParseNode getBody()
	{
		return this.getChild(1);
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
