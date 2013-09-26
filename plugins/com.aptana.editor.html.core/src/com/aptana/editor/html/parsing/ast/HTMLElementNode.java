/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Platform;

import beaver.Symbol;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.editor.html.parsing.HTMLParserConstants;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseNodeAttribute;
import com.aptana.parsing.lexer.IRange;

public class HTMLElementNode extends HTMLNode
{

	private static final String ID = "id"; //$NON-NLS-1$
	private static final String CLASS = "class"; //$NON-NLS-1$

	private final String fTag; // i.e.: name

	private int fStartNodeOffset;
	private int fStartNodeEnd;

	private int fEndNodeOffset;
	private int fEndNodeEnd;

	/**
	 * Note: lazily-initialized to save on memory.
	 */
	private Map<String, IParseNodeAttribute> fAttributes;

	/**
	 * Note: lazily-initialized to save on memory.
	 */
	private ArrayList<IParseNode> fCSSStyleNodes;

	/**
	 * Note: lazily-initialized to save on memory.
	 */
	private ArrayList<IParseNode> fJSAttributeNodes;

	private boolean fIsSelfClosing;

	public HTMLElementNode(Symbol tagSymbol, int start, int end)
	{
		this(tagSymbol, HTMLParserConstants.NO_HTML_NODES, start, end);
	}

	public HTMLElementNode(Symbol tagSymbol, HTMLNode[] children, int start, int end)
	{
		super(IHTMLNodeTypes.ELEMENT, children, start, end);
		String tag = tagSymbol.value.toString();
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
		this.fTag = tag;
		// FIXME this is actually the range of the entire start tag. I'd "fix" it, but I think a lot of code relies on
		// this behavior right now
		this.fStartNodeOffset = tagSymbol.getStart();
		this.fStartNodeEnd = tagSymbol.getEnd();
	}

	@Override
	public void trimToSize()
	{
		super.trimToSize();
		if (fCSSStyleNodes != null)
		{
			fCSSStyleNodes.trimToSize();
		}
		if (fJSAttributeNodes != null)
		{
			fJSAttributeNodes.trimToSize();
		}
	}

	@Override
	public void addOffset(int offset)
	{
		fStartNodeOffset += offset;
		fStartNodeEnd += offset;
		super.addOffset(offset);
	}

	public void addCSSStyleNode(IParseNode node)
	{
		if (fCSSStyleNodes == null)
		{
			fCSSStyleNodes = new ArrayList<IParseNode>(3);
		}
		fCSSStyleNodes.add(node);
	}

	public void addJSAttributeNode(IParseNode node)
	{
		if (fJSAttributeNodes == null)
		{
			fJSAttributeNodes = new ArrayList<IParseNode>(3);
		}
		fJSAttributeNodes.add(node);
	}

	public String getName()
	{
		return this.fTag;
	}

	@Override
	public String getElementName()
	{
		return getName();
	}

	@Override
	public INameNode getNameNode()
	{
		return new NameNode(fTag, fStartNodeOffset, fStartNodeEnd);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getNodeAtOffset(int)
	 */
	@Override
	public IParseNode getNodeAtOffset(int offset)
	{
		IParseNode result = super.getNodeAtOffset(offset);

		if (result == this && fJSAttributeNodes != null)
		{
			for (IParseNode node : fJSAttributeNodes)
			{
				if (node.contains(offset))
				{
					result = node.getNodeAtOffset(offset);
					break;
				}
			}
		}

		if (result == this && fCSSStyleNodes != null)
		{
			for (IParseNode node : fCSSStyleNodes)
			{
				if (node.contains(offset))
				{
					result = node.getNodeAtOffset(offset);
					break;
				}
			}
		}

		return result;
	}

	@Override
	public String getText()
	{
		StringBuilder text = new StringBuilder();
		text.append(getName());
		List<String> attributes = getAttributesToShow();
		for (String attribute : attributes)
		{
			// we show id and class differently from other attributes in the outline
			if (ID.equals(attribute))
			{
				String id = getID();
				if (id != null)
				{
					text.append('#').append(id);
				}
			}
			else if (CLASS.equals(attribute))
			{
				String cssClass = getCSSClass();
				if (cssClass != null)
				{
					text.append('.').append(cssClass);
				}
			}
			else
			{
				if (fAttributes != null)
				{
					IParseNodeAttribute value = fAttributes.get(attribute);
					if (value != null)
					{
						text.append(' ').append(value.getValue());
					}
				}
			}
		}
		return text.toString();
	}

	public String getID()
	{
		return getAttributeValue(ID);
	}

	public String getCSSClass()
	{
		return getAttributeValue(CLASS);
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

	public void setAttribute(String name, String value, IRange nameRegion, IRange valueRegion)
	{
		if (fAttributes == null)
		{
			fAttributes = new HashMap<String, IParseNodeAttribute>(2);
		}
		fAttributes.put(name, new ParseNodeAttribute(this, name, value, nameRegion, valueRegion));
	}

	public INameNode getEndNode()
	{
		return new NameNode(fTag, fEndNodeOffset, fEndNodeEnd);
	}

	public void setEndNode(int start, int end)
	{
		fEndNodeOffset = start;
		fEndNodeEnd = end;
	}

	public IParseNode[] getCSSStyleNodes()
	{
		if (fCSSStyleNodes == null)
		{
			return HTMLParserConstants.NO_PARSE_NODES;
		}
		return fCSSStyleNodes.toArray(new IParseNode[fCSSStyleNodes.size()]);
	}

	public IParseNode[] getJSAttributeNodes()
	{
		if (fJSAttributeNodes == null)
		{
			return HTMLParserConstants.NO_PARSE_NODES;
		}
		return fJSAttributeNodes.toArray(new IParseNode[fJSAttributeNodes.size()]);
	}

	public boolean isSelfClosing()
	{
		return fIsSelfClosing;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof HTMLElementNode))
		{
			return false;
		}

		HTMLElementNode other = (HTMLElementNode) obj;
		if (!getName().equals(other.getName()))
		{
			return false;
		}
		if (fAttributes == null || other.fAttributes == null)
		{
			return fAttributes == other.fAttributes;
		}
		return fAttributes.equals(other.fAttributes);
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		hash = 31 * hash + getName().hashCode();
		hash = 31 * hash + (fAttributes != null ? fAttributes.hashCode() : 1);
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		String name = getName();
		if (name.length() > 0)
		{
			text.append('<').append(name);
			if (fAttributes != null)
			{
				for (IParseNodeAttribute attr : fAttributes.values())
				{
					text.append(' ').append(attr.getName()).append("=\"").append(attr.getValue()).append('"'); //$NON-NLS-1$
				}
			}
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

	private static String getTagName(String tag)
	{
		StringTokenizer token = new StringTokenizer(tag);
		return token.nextToken();
	}

	private static List<String> getAttributesToShow()
	{
		// FIXME: Migrating preferences is a different story. Avoid it now.
		String value = Platform.getPreferencesService().getString(IPreferenceConstants.PREFERNCES_NODE,
				IPreferenceConstants.HTML_OUTLINE_TAG_ATTRIBUTES_TO_SHOW, StringUtil.EMPTY, null);
		StringTokenizer st = new StringTokenizer(value);
		List<String> attributes = new ArrayList<String>();
		while (st.hasMoreTokens())
		{
			attributes.add(st.nextToken());
		}
		return attributes;
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
}
