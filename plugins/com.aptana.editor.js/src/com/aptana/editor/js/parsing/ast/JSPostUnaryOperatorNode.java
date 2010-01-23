package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.parsing.lexer.JSTokens;

public class JSPostUnaryOperatorNode extends JSUnaryOperatorNode
{

	public JSPostUnaryOperatorNode(JSNode expression, String operator, int start, int end)
	{
		super(expression, start, end);

		short type = DEFAULT_TYPE;
		short token = JSTokens.getToken(operator);
		switch (token)
		{
			case JSTokens.MINUS_MINUS:
				type = JSNodeTypes.POST_DECREMENT;
				break;
			case JSTokens.PLUS_PLUS:
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
