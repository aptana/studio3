package com.aptana.editor.js.parsing.ast;

public class JSPrimitiveNode extends JSNode
{
	private String fText;

	public JSPrimitiveNode(short type, String text, int start, int end)
	{
		super(type, start, end);
		fText = text;
	}

	@Override
	public String toString()
	{
		return appendSemicolon(fText);
	}
}
