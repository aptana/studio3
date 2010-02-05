package com.aptana.editor.css.parsing.ast;

public class CSSCharSetNode extends CSSNode
{

	private String fEncoding;
	private String fText;

	public CSSCharSetNode(String encoding, int start, int end)
	{
		super(start, end);
		fEncoding = encoding;
	}

	@Override
	public String toString()
	{
		if (fText == null)
		{
			StringBuilder buf = new StringBuilder();
			buf.append("@charset ").append(fEncoding).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
			fText = buf.toString();
		}
		return fText;
	}
}
