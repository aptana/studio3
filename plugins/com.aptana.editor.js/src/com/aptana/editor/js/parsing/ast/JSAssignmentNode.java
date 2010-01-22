package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.parsing.lexer.JSTokens;
import com.aptana.parsing.ast.IParseNode;

public class JSAssignmentNode extends JSNode
{

	public JSAssignmentNode(JSNode left, String assignOperator, JSNode right)
	{
		this.start = left.getStart();
		this.end = right.getEnd();

		short type = DEFAULT_TYPE;
		short token = JSTokens.getToken(assignOperator);
		switch (token)
		{
			case JSTokens.EQUAL:
				type = JSNodeTypes.ASSIGN;
				break;
			case JSTokens.PLUS_EQUAL:
				type = JSNodeTypes.ADD_AND_ASSIGN;
				break;
			case JSTokens.GREATER_GREATER_GREATER_EQUAL:
				type = JSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN;
				break;
			case JSTokens.AMPERSAND_EQUAL:
				type = JSNodeTypes.BITWISE_AND_AND_ASSIGN;
				break;
			case JSTokens.PIPE_EQUAL:
				type = JSNodeTypes.BITWISE_OR_AND_ASSIGN;
				break;
			case JSTokens.CARET_EQUAL:
				type = JSNodeTypes.BITWISE_XOR_AND_ASSIGN;
				break;
			case JSTokens.FORWARD_SLASH_EQUAL:
				type = JSNodeTypes.DIVIDE_AND_ASSIGN;
				break;
			case JSTokens.PERCENT_EQUAL:
				type = JSNodeTypes.MOD_AND_ASSIGN;
				break;
			case JSTokens.STAR_EQUAL:
				type = JSNodeTypes.MULTIPLY_AND_ASSIGN;
				break;
			case JSTokens.LESS_LESS_EQUAL:
				type = JSNodeTypes.SHIFT_LEFT_AND_ASSIGN;
				break;
			case JSTokens.GREATER_GREATER_EQUAL:
				type = JSNodeTypes.SHIFT_RIGHT_AND_ASSIGN;
				break;
			case JSTokens.MINUS_EQUAL:
				type = JSNodeTypes.SUBTRACT_AND_ASSIGN;
				break;
		}
		setType(type);

		setChildren(new JSNode[] { left, right });
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		String operator = "???"; //$NON-NLS-1$
		switch (getType())
		{
			case JSNodeTypes.ASSIGN:
				operator = "="; //$NON-NLS-1$
				break;
			case JSNodeTypes.ADD_AND_ASSIGN:
				operator = "+="; //$NON-NLS-1$
				break;
			case JSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN:
				operator = ">>>="; //$NON-NLS-1$
				break;
			case JSNodeTypes.BITWISE_AND_AND_ASSIGN:
				operator = "&="; //$NON-NLS-1$
				break;
			case JSNodeTypes.BITWISE_OR_AND_ASSIGN:
				operator = "|="; //$NON-NLS-1$
				break;
			case JSNodeTypes.BITWISE_XOR_AND_ASSIGN:
				operator = "^="; //$NON-NLS-1$
				break;
			case JSNodeTypes.DIVIDE_AND_ASSIGN:
				operator = "/="; //$NON-NLS-1$
				break;
			case JSNodeTypes.MOD_AND_ASSIGN:
				operator = "%="; //$NON-NLS-1$
				break;
			case JSNodeTypes.MULTIPLY_AND_ASSIGN:
				operator = "*="; //$NON-NLS-1$
				break;
			case JSNodeTypes.SHIFT_LEFT_AND_ASSIGN:
				operator = "<<="; //$NON-NLS-1$
				break;
			case JSNodeTypes.SHIFT_RIGHT_AND_ASSIGN:
				operator = ">>="; //$NON-NLS-1$
				break;
			case JSNodeTypes.SUBTRACT_AND_ASSIGN:
				operator = "-="; //$NON-NLS-1$
				break;

		}
		IParseNode[] children = getChildren();
		text.append(children[0]);
		text.append(" ").append(operator).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
		text.append(children[1]);

		return appendSemicolon(text.toString());
	}
}
