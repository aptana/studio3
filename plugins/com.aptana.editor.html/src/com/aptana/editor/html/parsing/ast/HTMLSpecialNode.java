package com.aptana.editor.html.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

public class HTMLSpecialNode extends HTMLNode
{

	private String fTagName;

	public HTMLSpecialNode(String tagName, IParseNode[] children, int start, int end)
	{
		super(HTMLNodeTypes.SPECIAL, start, end);
		fTagName = tagName;
		setChildren(children);
	}

	public String getLanguageTag()
	{
		return fTagName;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof HTMLSpecialNode))
			return false;

		return getLanguageTag().equals(((HTMLSpecialNode) obj).getLanguageTag());
	}

	@Override
	public int hashCode()
	{
		return 31 * super.hashCode() + getLanguageTag().hashCode();
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
