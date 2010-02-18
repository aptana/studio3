package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.parsing.lexer.JSTokens;
import com.aptana.parsing.ast.IParseNode;

public class JSBinaryOperatorNode extends JSNode
{

	protected JSBinaryOperatorNode(JSNode left, JSNode right)
	{
		this.start = left.getStart();
		this.end = right.getEnd();

		setChildren(new JSNode[] { left, right });
	}

	public JSBinaryOperatorNode(JSNode left, String operator, JSNode right)
	{
		this(left, right);

		short type = DEFAULT_TYPE;
		short token = JSTokens.getToken(operator);
		switch (token)
		{
			case JSTokens.EQUAL_EQUAL:
				type = JSNodeTypes.EQUAL;
				break;
			case JSTokens.GREATER:
				type = JSNodeTypes.GREATER_THAN;
				break;
			case JSTokens.GREATER_EQUAL:
				type = JSNodeTypes.GREATER_THAN_OR_EQUAL;
				break;
			case JSTokens.EQUAL_EQUAL_EQUAL:
				type = JSNodeTypes.IDENTITY;
				break;
			case JSTokens.IN:
				type = JSNodeTypes.IN;
				break;
			case JSTokens.INSTANCEOF:
				type = JSNodeTypes.INSTANCE_OF;
				break;
			case JSTokens.LESS:
				type = JSNodeTypes.LESS_THAN;
				break;
			case JSTokens.LESS_EQUAL:
				type = JSNodeTypes.LESS_THAN_OR_EQUAL;
				break;
			case JSTokens.AMPERSAND_AMPERSAND:
				type = JSNodeTypes.LOGICAL_AND;
				break;
			case JSTokens.PIPE_PIPE:
				type = JSNodeTypes.LOGICAL_OR;
				break;
			case JSTokens.EXCLAMATION_EQUAL:
				type = JSNodeTypes.NOT_EQUAL;
				break;
			case JSTokens.EXCLAMATION_EQUAL_EQUAL:
				type = JSNodeTypes.NOT_IDENTITY;
				break;
			case JSTokens.PLUS:
				type = JSNodeTypes.ADD;
				break;
			case JSTokens.GREATER_GREATER_GREATER:
				type = JSNodeTypes.ARITHMETIC_SHIFT_RIGHT;
				break;
			case JSTokens.AMPERSAND:
				type = JSNodeTypes.BITWISE_AND;
				break;
			case JSTokens.PIPE:
				type = JSNodeTypes.BITWISE_OR;
				break;
			case JSTokens.CARET:
				type = JSNodeTypes.BITWISE_XOR;
				break;
			case JSTokens.FORWARD_SLASH:
				type = JSNodeTypes.DIVIDE;
				break;
			case JSTokens.PERCENT:
				type = JSNodeTypes.MOD;
				break;
			case JSTokens.STAR:
				type = JSNodeTypes.MULTIPLY;
				break;
			case JSTokens.LESS_LESS:
				type = JSNodeTypes.SHIFT_LEFT;
				break;
			case JSTokens.GREATER_GREATER:
				type = JSNodeTypes.SHIFT_RIGHT;
				break;
			case JSTokens.MINUS:
				type = JSNodeTypes.SUBTRACT;
				break;
		}
		setType(type);
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		String operator = "??"; //$NON-NLS-1$
		switch (getType())
		{
			case JSNodeTypes.EQUAL:
				operator = "=="; //$NON-NLS-1$
				break;
			case JSNodeTypes.GREATER_THAN:
				operator = ">"; //$NON-NLS-1$
				break;
			case JSNodeTypes.GREATER_THAN_OR_EQUAL:
				operator = ">="; //$NON-NLS-1$
				break;
			case JSNodeTypes.IDENTITY:
				operator = "==="; //$NON-NLS-1$
				break;
			case JSNodeTypes.IN:
				operator = "in"; //$NON-NLS-1$
				break;
			case JSNodeTypes.INSTANCE_OF:
				operator = "instanceof"; //$NON-NLS-1$
				break;
			case JSNodeTypes.LESS_THAN:
				operator = "<"; //$NON-NLS-1$
				break;
			case JSNodeTypes.LESS_THAN_OR_EQUAL:
				operator = "<="; //$NON-NLS-1$
				break;
			case JSNodeTypes.LOGICAL_AND:
				operator = "&&"; //$NON-NLS-1$
				break;
			case JSNodeTypes.LOGICAL_OR:
				operator = "||"; //$NON-NLS-1$
				break;
			case JSNodeTypes.NOT_EQUAL:
				operator = "!="; //$NON-NLS-1$
				break;
			case JSNodeTypes.NOT_IDENTITY:
				operator = "!=="; //$NON-NLS-1$
				break;
			case JSNodeTypes.ADD:
				operator = "+"; //$NON-NLS-1$
				break;
			case JSNodeTypes.ARITHMETIC_SHIFT_RIGHT:
				operator = ">>>"; //$NON-NLS-1$
				break;
			case JSNodeTypes.BITWISE_AND:
				operator = "&"; //$NON-NLS-1$
				break;
			case JSNodeTypes.BITWISE_OR:
				operator = "|"; //$NON-NLS-1$
				break;
			case JSNodeTypes.BITWISE_XOR:
				operator = "^"; //$NON-NLS-1$
				break;
			case JSNodeTypes.DIVIDE:
				operator = "/"; //$NON-NLS-1$
				break;
			case JSNodeTypes.MOD:
				operator = "%"; //$NON-NLS-1$
				break;

			case JSNodeTypes.MULTIPLY:
				operator = "*"; //$NON-NLS-1$
				break;
			case JSNodeTypes.SHIFT_LEFT:
				operator = "<<"; //$NON-NLS-1$
				break;

			case JSNodeTypes.SHIFT_RIGHT:
				operator = ">>"; //$NON-NLS-1$
				break;

			case JSNodeTypes.SUBTRACT:
				operator = "-"; //$NON-NLS-1$
				break;
		}
		IParseNode[] children = getChildren();
		text.append(children[0]);
		text.append(" ").append(operator).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
		text.append(children[1]);

		return appendSemicolon(text.toString());
	}
}
