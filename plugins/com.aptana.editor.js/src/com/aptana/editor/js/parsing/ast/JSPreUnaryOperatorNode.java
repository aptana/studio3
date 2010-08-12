package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.parsing.ast.IParseNode;

public class JSPreUnaryOperatorNode extends JSNode
{
	private Symbol _operator;

	/**
	 * JSUnaryOperatorNode
	 * 
	 * @param type
	 * @param expression
	 */
	protected JSPreUnaryOperatorNode(short type, JSNode expression)
	{
		this.setChildren(new JSNode[] { expression });

		this.setNodeType(type);
	}

	/**
	 * JSUnaryOperatorNode
	 * 
	 * @param operator
	 * @param expression
	 */
	public JSPreUnaryOperatorNode(Symbol operator, JSNode expression)
	{
		this._operator = operator;
		this.setChildren(new JSNode[] { expression });

		short type;
		JSTokenType token = JSTokenType.get((String) operator.value);

		switch (token)
		{
			case DELETE:
				type = JSNodeTypes.DELETE;
				break;

			case EXCLAMATION:
				type = JSNodeTypes.LOGICAL_NOT;
				break;

			case MINUS:
				type = JSNodeTypes.NEGATIVE;
				break;

			case MINUS_MINUS:
				type = JSNodeTypes.PRE_DECREMENT;
				break;

			case PLUS:
				type = JSNodeTypes.POSITIVE;
				break;

			case PLUS_PLUS:
				type = JSNodeTypes.PRE_INCREMENT;
				break;

			case TILDE:
				type = JSNodeTypes.BITWISE_NOT;
				break;

			case TYPEOF:
				type = JSNodeTypes.TYPEOF;
				break;

			case VOID:
				type = JSNodeTypes.VOID;
				break;

			default:
				throw new IllegalArgumentException(Messages.JSPreUnaryOperatorNode_0 + token);
		}

		setNodeType(type);
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
	 * getExpression
	 * 
	 * @return
	 */
	public IParseNode getExpression()
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
}
