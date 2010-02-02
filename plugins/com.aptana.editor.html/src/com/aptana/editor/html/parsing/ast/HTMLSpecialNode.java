package com.aptana.editor.html.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class HTMLSpecialNode extends HTMLNode
{

	public static final short CSS = 0;
	public static final short JS = 1;

	private short fNestedLanguage;

	public HTMLSpecialNode(short language, IParseNode[] children, int start, int end)
	{
		super(HTMLNodeTypes.SPECIAL, start, end);
		fNestedLanguage = language;
		setChildren(children);
	}

	public String getLanguageTag()
	{
		switch (getNestedLanguage())
		{
			case CSS:
				return "style"; //$NON-NLS-1$
			case JS:
				return "script"; //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

	public short getNestedLanguage()
	{
		return fNestedLanguage;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		String tag = getLanguageTag();
		text.append("<").append(tag).append(">"); //$NON-NLS-1$ //$NON-NLS-2$
		text.append(super.toString());
		text.append("</").append(tag).append(">"); //$NON-NLS-1$ //$NON-NLS-2$

		return text.toString();
	}
}
