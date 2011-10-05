/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.parsing.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.aptana.editor.xml.parsing.XMLParser;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.ParseNodeAttribute;
import com.aptana.parsing.lexer.IRange;

public class XMLElementNode extends XMLNode
{
	/**
	 * getTagName
	 * 
	 * @param tag
	 * @return
	 */
	private static String getTagName(String tag)
	{
		// the first element is the tag name
		StringTokenizer token = new StringTokenizer(tag);
		return token.nextToken();
	}

	private INameNode fNameNode;
	private boolean fIsSelfClosing;
	private Map<String, String> fAttributes;

	/**
	 * XMLElementNode
	 * 
	 * @param tag
	 * @param start
	 * @param end
	 */
	public XMLElementNode(String tag, int start, int end)
	{
		this(tag, XMLParser.NO_XML_NODES, start, end);
	}

	/**
	 * XMLElementNode
	 * 
	 * @param tag
	 * @param children
	 * @param start
	 * @param end
	 */
	public XMLElementNode(String tag, XMLNode[] children, int start, int end)
	{
		super(XMLNodeType.ELEMENT, children, start, end);

		if (tag.length() > 0)
		{
			try
			{
				if (tag.endsWith("/>")) //$NON-NLS-1$
				{
					// self-closing
					tag = getTagName(tag.substring(1, tag.length() - 2));
					fIsSelfClosing = true;
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

		fNameNode = new NameNode(tag, start, end);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#addOffset(int)
	 */
	@Override
	public void addOffset(int offset)
	{
		IRange range = fNameNode.getNameRange();

		fNameNode = new NameNode(fNameNode.getName(), range.getStartingOffset() + offset, range.getEndingOffset() + offset);

		super.addOffset(offset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#equals(java.lang.Object)
	 */
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

	/**
	 * getAttribute
	 * 
	 * @param name
	 * @return
	 */
	public String getAttibute(String name)
	{
		String result = ""; //$NON-NLS-1$

		if (fAttributes != null)
		{
			result = fAttributes.get(name);
		}

		return result;
	}
	

	/* (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getAttributes()
	 */
	@Override
	public IParseNodeAttribute[] getAttributes()
	{
		List<IParseNodeAttribute> attributes = new ArrayList<IParseNodeAttribute>();
		
		// NOTE: May want to cache this
		if (fAttributes != null && fAttributes.size() > 0)
		{
			for (Map.Entry<String, String> entry : fAttributes.entrySet())
			{
				attributes.add(new ParseNodeAttribute(this, entry.getKey(), entry.getValue()));
			}
		}
		
		return attributes.toArray(new IParseNodeAttribute[attributes.size()]);
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return fNameNode.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getNameNode()
	 */
	@Override
	public INameNode getNameNode()
	{
		return fNameNode;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getText()
	 */
	@Override
	public String getText()
	{
		return fNameNode.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return 31 * super.hashCode() + getName().hashCode();
	}

	/**
	 * isSelfClosing
	 * 
	 * @return
	 */
	public boolean isSelfClosing()
	{
		return fIsSelfClosing;
	}

	/**
	 * setAttribute
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, String value)
	{
		if (fAttributes == null)
		{
			// NOTE: use linked hash map to preserve add order
			fAttributes = new LinkedHashMap<String, String>();
		}

		fAttributes.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		String name = getName();

		if (name.length() > 0)
		{
			text.append('<').append(name);
			text.append('>');

			IParseNode[] children = getChildren();

			for (IParseNode child : children)
			{
				text.append(child);
			}

			text.append("</").append(name).append('>'); //$NON-NLS-1$
		}

		return text.toString();
	}
}
