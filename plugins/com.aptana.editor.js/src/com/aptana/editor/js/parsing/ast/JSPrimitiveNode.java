package com.aptana.editor.js.parsing.ast;

import com.aptana.editor.js.parsing.lexer.JSTokens;

public class JSPrimitiveNode extends JSNode
{
	private String fText;

	public JSPrimitiveNode(short type, int start, int end)
	{
		this(JSTokens.getTokenName(type), start, end);
	}

	public JSPrimitiveNode(String text, int start, int end)
	{
		fText = text;
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString()
	{
		return fText;
	}
}
