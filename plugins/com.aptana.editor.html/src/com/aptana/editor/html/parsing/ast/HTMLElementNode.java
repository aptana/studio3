package com.aptana.editor.html.parsing.ast;

import java.util.StringTokenizer;

import beaver.Symbol;

import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class HTMLElementNode extends HTMLNode
{

	private static final class NameNode implements INameNode
	{

		private final String fName;
		private final IRange fRange;

		public NameNode(String name, int start, int end)
		{
			fName = name;
			fRange = new Range(start, end);
		}

		@Override
		public String getName()
		{
			return fName;
		}

		@Override
		public IRange getNameRange()
		{
			return fRange;
		}
	}

	private INameNode fNameNode;

	public HTMLElementNode(Symbol tagSymbol, int start, int end)
	{
		this(tagSymbol, new HTMLNode[0], start, end);
	}

	public HTMLElementNode(Symbol tagSymbol, HTMLNode[] children, int start, int end)
	{
		super(HTMLNodeTypes.ELEMENT, children, start, end);
		String tag = tagSymbol.value.toString();
		if (tag.length() > 0)
		{
			try
			{
				if (tag.endsWith("/>")) //$NON-NLS-1$
				{
					// self-closing
					tag = getTagName(tag.substring(1, tag.length() - 2));
				}
				else
				{
					tag = getTagName(tag.substring(1, tag.length() - 1));
				}
			}
			catch (IndexOutOfBoundsException e)
			{
			}
		}
		fNameNode = new NameNode(tag, tagSymbol.getStart(), tagSymbol.getEnd());
	}

	public String getName()
	{
		return fNameNode.getName();
	}

	@Override
	public INameNode getNameNode()
	{
		return fNameNode;
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
		String name = getName();
		if (name.length() > 0)
		{
			text.append("<").append(name); //$NON-NLS-1$
			text.append(">"); //$NON-NLS-1$
			IParseNode[] children = getChildren();
			for (IParseNode child : children)
			{
				text.append(child);
			}
			text.append("</").append(name).append(">"); //$NON-NLS-1$//$NON-NLS-2$
		}
		return text.toString();
	}

	private static String getTagName(String tag)
	{
		StringTokenizer token = new StringTokenizer(tag);
		return token.nextToken();
	}
}
