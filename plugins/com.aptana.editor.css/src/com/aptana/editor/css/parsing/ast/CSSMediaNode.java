package com.aptana.editor.css.parsing.ast;

import java.util.Arrays;

public class CSSMediaNode extends CSSNode
{

	private String[] fMedias;
	private String fText;

	public CSSMediaNode(String[] medias, int start, int end)
	{
		super(start, end);
		fMedias = medias;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSMediaNode))
		{
			return false;
		}
		CSSMediaNode other = (CSSMediaNode) obj;
		return Arrays.equals(fMedias, other.fMedias);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + Arrays.hashCode(fMedias);
	}

	@Override
	public String toString()
	{
		if (fText == null)
		{
			StringBuilder text = new StringBuilder();
			text.append("@media"); //$NON-NLS-1$
			for (String media : fMedias)
			{
				text.append(" ").append(media); //$NON-NLS-1$
			}
			text.append("{").append("}"); //$NON-NLS-1$ //$NON-NLS-2$
			fText = text.toString();
		}
		return fText;
	}
}
