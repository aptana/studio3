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
	
	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof JSPrimitiveNode))
			return false;

		return fText.equals(((JSPrimitiveNode) obj).fText);
	}

	@Override
	public int hashCode()
	{
		return 31 * super.hashCode() + fText.hashCode();
	}
}
