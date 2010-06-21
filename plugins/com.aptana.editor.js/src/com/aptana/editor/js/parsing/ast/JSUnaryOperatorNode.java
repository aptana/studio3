package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.parsing.lexer.JSTokenType;

public class JSUnaryOperatorNode extends JSNode
{
	/**
	 * JSUnaryOperatorNode
	 * 
	 * @param start
	 * @param end
	 * @param expression
	 */
	protected JSUnaryOperatorNode(int start, int end, JSNode expression)
	{
		setLocation(start, end);
		setChildren(new JSNode[] { expression });
	}

	/**
	 * JSUnaryOperatorNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 * @param expression
	 */
	public JSUnaryOperatorNode(short type, int start, int end, JSNode expression)
	{
		this(start, end, expression);
		setType(type);
	}

	/**
	 * JSUnaryOperatorNode
	 * 
	 * @param operator
	 * @param start
	 * @param end
	 * @param expression
	 */
	public JSUnaryOperatorNode(String operator, int start, int end, JSNode expression)
	{
		this(start, end, expression);

		short type = DEFAULT_TYPE;
		JSTokenType token = JSTokenType.get(operator);
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
		}
		setType(type);
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		JSNode expression = (JSNode) getChildren()[0];
		String operator = ""; //$NON-NLS-1$
		int type = getNodeType();
		switch (type)
		{
			case JSNodeTypes.DELETE:
				operator = "delete "; //$NON-NLS-1$
				break;
			case JSNodeTypes.LOGICAL_NOT:
				operator = "!"; //$NON-NLS-1$
				break;
			case JSNodeTypes.NEGATIVE:
				operator = "-"; //$NON-NLS-1$
				break;
			case JSNodeTypes.PRE_DECREMENT:
				operator = "--"; //$NON-NLS-1$
				break;
			case JSNodeTypes.POSITIVE:
				operator = "+"; //$NON-NLS-1$
				break;
			case JSNodeTypes.PRE_INCREMENT:
				operator = "++"; //$NON-NLS-1$
				break;
			case JSNodeTypes.BITWISE_NOT:
				operator = "~"; //$NON-NLS-1$
				break;
			case JSNodeTypes.TYPEOF:
				operator = "typeof"; //$NON-NLS-1$
				if (expression.getNodeType() != JSNodeTypes.GROUP)
				{
					operator += " "; //$NON-NLS-1$
				}
				break;
			case JSNodeTypes.VOID:
				operator = "void "; //$NON-NLS-1$
				break;
		}
		text.append(operator).append(expression);

		this.appendSemicolon(text);

		return text.toString();
	}
}
