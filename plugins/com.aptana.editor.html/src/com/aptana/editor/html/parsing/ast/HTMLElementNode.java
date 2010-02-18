package com.aptana.editor.html.parsing.ast;

import java.util.StringTokenizer;

import com.aptana.parsing.ast.IParseNode;

public class HTMLElementNode extends HTMLNode
{
	private String fName;

	public HTMLElementNode(String tag, int start, int end)
	{
		this(tag, new HTMLNode[0], start, end);
	}

	public HTMLElementNode(String tag, HTMLNode[] children, int start, int end)
	{
		super(HTMLNodeTypes.ELEMENT, children, start, end);
		fName = tag;
		if (tag.length() > 0)
		{
			try
			{
				if (tag.endsWith("/>")) //$NON-NLS-1$
				{
					// self-closing
					fName = getTagName(tag.substring(1, tag.length() - 2));
				}
				else
				{
					fName = getTagName(tag.substring(1, tag.length() - 1));
				}
			}
			catch (IndexOutOfBoundsException e)
			{
			}
		}
	}

	public String getName()
	{
		return fName;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof HTMLElementNode))
			return false;

		return getName().equals(((HTMLElementNode) obj).getName());
	}

	@Override
	public int hashCode()
	{
		return 31 * super.hashCode() + getName().hashCode();
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		if (fName.length() > 0)
		{
			text.append("<").append(fName); //$NON-NLS-1$
			text.append(">"); //$NON-NLS-1$
			IParseNode[] children = getChildren();
			for (IParseNode child : children)
			{
				text.append(child);
			}
			text.append("</").append(fName).append(">"); //$NON-NLS-1$//$NON-NLS-2$
		}
		return text.toString();
	}

	private static String getTagName(String tag)
	{
		StringTokenizer token = new StringTokenizer(tag);
		return token.nextToken();
	}
}
