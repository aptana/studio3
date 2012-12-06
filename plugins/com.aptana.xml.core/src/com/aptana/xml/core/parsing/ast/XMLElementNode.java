/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.parsing.ast;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import beaver.Symbol;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.ParseNodeAttribute;
import com.aptana.parsing.lexer.IRange;
import com.aptana.xml.core.parsing.Terminals;
import com.aptana.xml.core.parsing.XMLParser;

public class XMLElementNode extends XMLNode
{

	private INameNode fNameNode;
	private final boolean fIsSelfClosing;
	private Map<String, String> fAttributes;

	/**
	 * XMLElementNode
	 * 
	 * @param tag
	 * @param start
	 * @param end
	 */
	public XMLElementNode(String tag, int start, Symbol close)
	{
		super(XMLNodeType.ELEMENT, XMLParser.NO_XML_NODES, start, close.getEnd());

		fNameNode = new NameNode(tag, start, close.getEnd());
		fIsSelfClosing = (close.getId() == Terminals.SLASH_GREATER);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#addOffset(int)
	 */
	@Override
	public void addOffset(int offset)
	{
		IRange range = fNameNode.getNameRange();

		fNameNode = new NameNode(fNameNode.getName(), range.getStartingOffset() + offset, range.getEndingOffset()
				+ offset);

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
	public String getAttribute(String name)
	{
		if (fAttributes == null)
		{
			// TODO Return null to distinguish between no value and empty string?
			return StringUtil.EMPTY;
		}

		return fAttributes.get(name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getAttributes()
	 */
	@Override
	public IParseNodeAttribute[] getAttributes()
	{
		if (CollectionsUtil.isEmpty(fAttributes))
		{
			return NO_ATTRIBUTES;
		}
		final XMLElementNode self = this;
		List<IParseNodeAttribute> attributes = CollectionsUtil.map(fAttributes.entrySet(),
				new IMap<Map.Entry<String, String>, IParseNodeAttribute>()
				{

					public IParseNodeAttribute map(Entry<String, String> entry)
					{
						return new ParseNodeAttribute(self, entry.getKey(), entry.getValue());
					}
				});
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
		// FIXME Take in the ranges of the name and values!
		if (fAttributes == null)
		{
			// NOTE: use linked hash map to preserve add order
			fAttributes = new LinkedHashMap<String, String>();
		}

		fAttributes.put(name, StringUtil.stripQuotes(value));
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
