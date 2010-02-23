package com.aptana.editor.xml.parsing.ast;

import java.util.StringTokenizer;

import com.aptana.parsing.ast.IParseNode;

public class XMLElementNode extends XMLNode
{
	private String fName;
	private boolean fIsSelfClosing;

	public XMLElementNode(String tag, int start, int end)
	{
		this(tag, new XMLNode[0], start, end);
	}

	public XMLElementNode(String tag, XMLNode[] children, int start, int end)
	{
		super(XMLNodeTypes.ELEMENT.getIndex(), children, start, end);
		fName = tag;
		if (tag.length() > 0)
		{
			try
			{
				if (tag.endsWith("/>")) //$NON-NLS-1$
				{
					// self-closing
					fName = getTagName(tag.substring(1, tag.length() - 2));
					fIsSelfClosing = true;
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

	public boolean isSelfClosing()
	{
		return fIsSelfClosing;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
		{
			return false;
		}
		if (!(obj instanceof XMLElementNode))
		{
			return false;
		}

		return getName().equals(((XMLElementNode) obj).getName());
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
		// the first element is the tag name
		StringTokenizer token = new StringTokenizer(tag);
		return token.nextToken();
	}
}
