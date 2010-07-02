package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.parsing.ast.IParseNode;

public class JSAssignmentNode extends JSNode
{
	private Symbol _operator;

	/**
	 * JSAssignmentNode
	 * 
	 * @param left
	 * @param assignOperator
	 * @param right
	 */
	public JSAssignmentNode(JSNode left, Symbol assignOperator, JSNode right)
	{
		this._operator = assignOperator;

		short type = DEFAULT_TYPE;
		JSTokenType token = JSTokenType.get((String) assignOperator.value);

		switch (token)
		{
			case EQUAL:
				type = JSNodeTypes.ASSIGN;
				break;
			case PLUS_EQUAL:
				type = JSNodeTypes.ADD_AND_ASSIGN;
				break;
			case GREATER_GREATER_GREATER_EQUAL:
				type = JSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN;
				break;
			case AMPERSAND_EQUAL:
				type = JSNodeTypes.BITWISE_AND_AND_ASSIGN;
				break;
			case PIPE_EQUAL:
				type = JSNodeTypes.BITWISE_OR_AND_ASSIGN;
				break;
			case CARET_EQUAL:
				type = JSNodeTypes.BITWISE_XOR_AND_ASSIGN;
				break;
			case FORWARD_SLASH_EQUAL:
				type = JSNodeTypes.DIVIDE_AND_ASSIGN;
				break;
			case PERCENT_EQUAL:
				type = JSNodeTypes.MOD_AND_ASSIGN;
				break;
			case STAR_EQUAL:
				type = JSNodeTypes.MULTIPLY_AND_ASSIGN;
				break;
			case LESS_LESS_EQUAL:
				type = JSNodeTypes.SHIFT_LEFT_AND_ASSIGN;
				break;
			case GREATER_GREATER_EQUAL:
				type = JSNodeTypes.SHIFT_RIGHT_AND_ASSIGN;
				break;
			case MINUS_EQUAL:
				type = JSNodeTypes.SUBTRACT_AND_ASSIGN;
				break;
		}

		this.setNodeType(type);
		this.setChildren(new JSNode[] { left, right });
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
}
