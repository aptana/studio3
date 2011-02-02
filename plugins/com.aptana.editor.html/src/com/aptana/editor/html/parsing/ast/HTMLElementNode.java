/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import beaver.Symbol;

import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class HTMLElementNode extends HTMLNode
{

	private static final String ID = "id"; //$NON-NLS-1$
	private static final String CLASS = "class"; //$NON-NLS-1$

	private INameNode fNameNode;
	private INameNode fEndNode;
	private Map<String, String> fAttributes;
	private List<IParseNode> fCSSStyleNodes;
	private List<IParseNode> fJSAttributeNodes;

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
		fAttributes = new HashMap<String, String>();
		fCSSStyleNodes = new ArrayList<IParseNode>();
		fJSAttributeNodes = new ArrayList<IParseNode>();
	}

	@Override
	public void addOffset(int offset)
	{
		IRange range = fNameNode.getNameRange();
		fNameNode = new NameNode(fNameNode.getName(), range.getStartingOffset() + offset, range.getEndingOffset()
				+ offset);
		super.addOffset(offset);
	}

	public void addCSSStyleNode(IParseNode node)
	{
		fCSSStyleNodes.add(node);
	}

	public void addJSAttributeNode(IParseNode node)
	{
		fJSAttributeNodes.add(node);
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
	public String getText()
	{
		StringBuilder text = new StringBuilder();
		text.append(getName());
		if (getID() != null)
		{
			text.append("#").append(getID()); //$NON-NLS-1$
		}
		if (getCSSClass() != null)
		{
			text.append(".").append(getCSSClass()); //$NON-NLS-1$
		}
		return text.toString();
	}

	public String getID()
	{
		return fAttributes.get(ID);
	}

	public String getCSSClass()
	{
		return fAttributes.get(CLASS);
	}

	public String getAttributeValue(String name)
	{
		return fAttributes.get(name);
	}

	public void setAttribute(String name, String value)
	{
		fAttributes.put(name, value);
	}

	public INameNode getEndNode()
	{
		return fEndNode;
	}

	public void setEndNode(int start, int end)
	{
		fEndNode = new NameNode(fNameNode.getName(), start, end);
	}

	public IParseNode[] getCSSStyleNodes()
	{
		return fCSSStyleNodes.toArray(new IParseNode[fCSSStyleNodes.size()]);
	}

	public IParseNode[] getJSAttributeNodes()
	{
		return fJSAttributeNodes.toArray(new IParseNode[fJSAttributeNodes.size()]);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof HTMLElementNode))
		{
			return false;
		}

		HTMLElementNode other = (HTMLElementNode) obj;
		return getName().equals(other.getName()) && fAttributes.equals(other.fAttributes);
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		hash = 31 * hash + getName().hashCode();
		hash = 31 * hash + fAttributes.hashCode();
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		String name = getName();
		if (name.length() > 0)
		{
			text.append("<").append(name); //$NON-NLS-1$
			Iterator<String> iter = fAttributes.keySet().iterator();
			String key, value;
			while (iter.hasNext())
			{
				key = iter.next();
				value = fAttributes.get(key);
				text.append(" ").append(key).append("=\"").append(value).append("\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
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
