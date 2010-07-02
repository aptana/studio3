package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public abstract class JSBinaryOperatorNode extends JSNode
{
	private Symbol _operator;

	/**
	 * JSBinaryOperatorNode
	 * 
	 * @param left
	 * @param right
	 */
	protected JSBinaryOperatorNode(JSNode left, JSNode right)
	{
		this.start = left.getStart();
		this.end = right.getEnd();

		setChildren(new JSNode[] { left, right });
	}

	/**
	 * JSBinaryOperatorNode
	 * 
	 * @param left
	 * @param operator
	 * @param right
	 */
	protected JSBinaryOperatorNode(JSNode left, Symbol operator, JSNode right)
	{
		this(left, right);

		this._operator = operator;
	}

	/**
	 * getLeftHandSide
	 * 
	 * @return
	 */
	public IParseNode getLeftHandSide()
	{
		return this.getChild(0);
	}

	/**
	 * getOperator
	 * 
	 * @return
	 */
	public Symbol getOperator()
	{
		return this._operator;
	}

	/**
	 * getRightHandSide
	 * 
	 * @return
	 */
	public IParseNode getRightHandSide()
	{
		return this.getChild(1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();

		text.append(this.getLeftHandSide());
		text.append(" ").append(this._operator.value).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
		text.append(this.getRightHandSide());

		this.appendSemicolon(text);

		return text.toString();
	}
}
