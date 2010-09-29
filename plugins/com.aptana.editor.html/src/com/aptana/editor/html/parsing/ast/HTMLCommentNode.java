package com.aptana.editor.html.parsing.ast;

import com.aptana.editor.html.parsing.lexer.HTMLTokens;

public class HTMLCommentNode extends HTMLNode
{

	private String fText;

	public HTMLCommentNode(String text, int start, int end)
	{
		super(HTMLTokens.COMMENT, start, end);
		fText = text;
	}

	@Override
	public String getText()
	{
		return fText;
	}

	@Override
	public String toString()
	{
		return fText;
	}
}
