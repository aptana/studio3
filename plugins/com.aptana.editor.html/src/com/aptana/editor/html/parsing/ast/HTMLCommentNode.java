package com.aptana.editor.html.parsing.ast;


public class HTMLCommentNode extends HTMLNode
{

	private String fText;

	public HTMLCommentNode(String text, int start, int end)
	{
		super(HTMLNodeTypes.COMMENT, start, end);
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
