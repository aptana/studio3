/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.parsing.ast;

import java.util.LinkedHashMap;
import java.util.Map;

import beaver.Symbol;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.ObjectUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseNodeAttribute;
import com.aptana.parsing.lexer.IRange;
import com.aptana.xml.core.parsing.Terminals;
import com.aptana.xml.core.parsing.XMLParser;

public class XMLElementNode extends XMLNode
{

	private NameNode fNameNode;
	private final boolean fIsSelfClosing;
	private Map<String, IParseNodeAttribute> fAttributes;

	/**
	 * XMLElementNode
	 * 
	 * @param tag
	 * @param start
	 * @param end
	 */
	public XMLElementNode(Symbol tag, int start, Symbol close)
	{
		super(XMLNodeType.ELEMENT, XMLParser.NO_XML_NODES, start, close.getEnd());

		this.fNameNode = new NameNode((String) tag.value, tag.getStart(), tag.getEnd());
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

		return ObjectUtil.areEqual(getName(), ((XMLElementNode) obj).getName());
	}

	public String getAttributeValue(String name)
	{
		if (fAttributes == null)
		{
			return null;
		}
		IParseNodeAttribute attr = fAttributes.get(name);
		if (attr == null)
		{
			return null;
		}
		return attr.getValue();
	}

	@Override
	public IParseNodeAttribute[] getAttributes()
	{
		if (fAttributes == null)
		{
			return ParseNode.NO_ATTRIBUTES;
		}
		return fAttributes.values().toArray(new IParseNodeAttribute[fAttributes.size()]);
	}

	/**
	 * If the offset covers an attribute's name or value ranges, return the attribute.
	 * 
	 * @param offset
	 * @return
	 */
	public IParseNodeAttribute getAttributeAtOffset(int offset)
	{
		IParseNodeAttribute[] attrs = getAttributes();
		if (ArrayUtil.isEmpty(attrs))
		{
			return null;
		}

		for (IParseNodeAttribute attr : attrs)
		{
			IRange nameRange = attr.getNameRange();
			if (nameRange != null && nameRange.contains(offset))
			{
				return attr;
			}

			IRange valueRange = attr.getValueRange();
			if (valueRange != null && valueRange.contains(offset))
			{
				return attr;
			}
		}
		return null;
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
		String name = getName();
		return 31 * super.hashCode() + (name == null ? 1 : getName().hashCode());
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
	 * @param range
	 * @param nameRegion
	 */
	public void setAttribute(String name, String value, IRange nameRange, IRange valueRange)
	{
		if (fAttributes == null)
		{
			// NOTE: use linked hash map to preserve add order
			fAttributes = new LinkedHashMap<String, IParseNodeAttribute>(2);
		}
		fAttributes.put(name, new ParseNodeAttribute(this, name, StringUtil.stripQuotes(value), nameRange, valueRange));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		String name = getName();
		if (StringUtil.isEmpty(name))
		{
			return StringUtil.EMPTY;
		}

		StringBuilder text = new StringBuilder();
		text.append('<').append(name).append('>');

		IParseNode[] children = getChildren();
		for (IParseNode child : children)
		{
			text.append(child);
		}

		text.append("</").append(name).append('>'); //$NON-NLS-1$
		return text.toString();
	}
}
