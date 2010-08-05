package com.aptana.editor.css.parsing.ast;

import java.util.Arrays;

public class CSSImportNode extends CSSNode
{

	private String fUriStr;
	private String[] fMediaList;

	public CSSImportNode(String uri, int start, int end)
	{
		this(uri, new String[0], start, end);
	}

	public CSSImportNode(String uri, String[] mediaList, int start, int end)
	{
		super(start, end);
		fUriStr = uri;
		fMediaList = mediaList;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSImportNode))
		{
			return false;
		}
		CSSImportNode other = (CSSImportNode) obj;
		return fUriStr.equals(other.fUriStr) && Arrays.equals(fMediaList, other.fMediaList);
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		hash = hash * 31 + fUriStr.hashCode();
		hash = hash * 31 + Arrays.hashCode(fMediaList);
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append("@import ").append(fUriStr); //$NON-NLS-1$
		for (int i = 0; i < fMediaList.length; ++i)
		{
			text.append(" ").append(fMediaList[i]); //$NON-NLS-1$
			if (i < fMediaList.length - 1)
			{
				text.append(","); //$NON-NLS-1$
			}
		}
		text.append(";"); //$NON-NLS-1$
		return text.toString();
	}
}
