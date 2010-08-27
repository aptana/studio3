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
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSCharSetNode))
		{
			return false;
		}
		CSSCharSetNode other = (CSSCharSetNode) obj;
		return fEncoding.equals(other.fEncoding);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + fEncoding.hashCode();
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
