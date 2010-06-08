package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.parsing.lexer.JSTokenType;

public class JSPostUnaryOperatorNode extends JSUnaryOperatorNode
{
	/**
	 * JSPostUnaryOperatorNode
	 * 
	 * @param operator
	 * @param start
	 * @param end
	 * @param expression
	 */
	public JSPostUnaryOperatorNode(String operator, int start, int end, JSNode expression)
	{
		super(start, end, expression);

		short type = DEFAULT_TYPE;
		JSTokenType token = JSTokenType.get(operator);
		switch (token)
		{
			case MINUS_MINUS:
				type = JSNodeTypes.POST_DECREMENT;
				break;
			case PLUS_PLUS:
				type = JSNodeTypes.POST_INCREMENT;
				break;
		}
		setType(type);
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append(getChildren()[0]);
		String operator = ""; //$NON-NLS-1$
		switch (getType())
		{
			case JSNodeTypes.POST_DECREMENT:
				operator = "--"; //$NON-NLS-1$
				break;
			case JSNodeTypes.POST_INCREMENT:
				operator = "++"; //$NON-NLS-1$
				break;
		}
		text.append(operator);

		return appendSemicolon(text.toString());
	}
}
