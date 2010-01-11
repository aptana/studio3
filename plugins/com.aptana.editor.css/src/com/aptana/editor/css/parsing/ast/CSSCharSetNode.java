package com.aptana.editor.css.parsing.ast;

public class CSSCharSetNode extends CSSNode
{

	private String fEncoding;

	public CSSCharSetNode(String encoding, int start, int end)
	{
		fEncoding = encoding;
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("@charset ").append(fEncoding).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
		return buf.toString();
	}
}
