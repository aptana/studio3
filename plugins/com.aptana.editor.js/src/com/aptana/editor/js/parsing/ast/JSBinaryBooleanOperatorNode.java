package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.JSTokenType;

public class JSBinaryBooleanOperatorNode extends JSBinaryOperatorNode
{
	/**
	 * JSBooleanOperatorNode
	 * 
	 * @param left
	 * @param operator
	 * @param right
	 */
	public JSBinaryBooleanOperatorNode(JSNode left, Symbol operator, JSNode right)
	{
		super(left, operator, right);

		JSTokenType token = JSTokenType.get((String) operator.value);
		short type;

		switch (token)
		{
			// equality operators
			case EQUAL_EQUAL:
				type = JSNodeTypes.EQUAL;
				break;

			case EXCLAMATION_EQUAL:
				type = JSNodeTypes.NOT_EQUAL;
				break;

			case EQUAL_EQUAL_EQUAL:
				type = JSNodeTypes.IDENTITY;
				break;

			case EXCLAMATION_EQUAL_EQUAL:
				type = JSNodeTypes.NOT_IDENTITY;
				break;

			// relational operators
			case LESS:
				type = JSNodeTypes.LESS_THAN;
				break;

			case GREATER:
				type = JSNodeTypes.GREATER_THAN;
				break;

			case LESS_EQUAL:
				type = JSNodeTypes.LESS_THAN_OR_EQUAL;
				break;

			case GREATER_EQUAL:
				type = JSNodeTypes.GREATER_THAN_OR_EQUAL;
				break;

			case INSTANCEOF:
				type = JSNodeTypes.INSTANCE_OF;
				break;

			case IN:
				type = JSNodeTypes.IN;
				break;

			// logical operators
			case AMPERSAND_AMPERSAND:
				type = JSNodeTypes.LOGICAL_AND;
				break;

			case PIPE_PIPE:
				type = JSNodeTypes.LOGICAL_OR;
				break;

			default:
				throw new IllegalArgumentException(Messages.JSBinaryBooleanOperatorNode_0 + token);
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
