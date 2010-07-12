package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.JSTokenType;

public class JSBinaryArithmeticOperatorNode extends JSBinaryOperatorNode
{
	/**
	 * JSArithmeticOperatorNode
	 * 
	 * @param left
	 * @param operator
	 * @param right
	 */
	public JSBinaryArithmeticOperatorNode(JSNode left, Symbol operator, JSNode right)
	{
		super(left, operator, right);

		JSTokenType token = JSTokenType.get((String) operator.value);
		short type;

		switch (token)
		{
			// additive operators
			case PLUS:
				type = JSNodeTypes.ADD;
				break;

			case MINUS:
				type = JSNodeTypes.SUBTRACT;
				break;

			// shift operators
			case LESS_LESS:
				type = JSNodeTypes.SHIFT_LEFT;
				break;

			case GREATER_GREATER:
				type = JSNodeTypes.SHIFT_RIGHT;
				break;

			case GREATER_GREATER_GREATER:
				type = JSNodeTypes.ARITHMETIC_SHIFT_RIGHT;
				break;

			// bit operator
			case AMPERSAND:
				type = JSNodeTypes.BITWISE_AND;
				break;

			case CARET:
				type = JSNodeTypes.BITWISE_XOR;
				break;

			case PIPE:
				type = JSNodeTypes.BITWISE_OR;
				break;

			// multiplicative operators
			case STAR:
				type = JSNodeTypes.MULTIPLY;
				break;

			case FORWARD_SLASH:
				type = JSNodeTypes.DIVIDE;
				break;

			case PERCENT:
				type = JSNodeTypes.MOD;
				break;

			default:
				throw new IllegalArgumentException(Messages.JSBinaryArithmeticOperatorNode_0 + token);
		}

		this.setNodeType(type);
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
}
