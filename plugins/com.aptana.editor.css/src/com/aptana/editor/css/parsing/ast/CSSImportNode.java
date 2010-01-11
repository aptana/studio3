package com.aptana.editor.css.parsing.ast;

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
		fUriStr = uri;
		fMediaList = mediaList;
		this.start = start;
		this.end = end;
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
