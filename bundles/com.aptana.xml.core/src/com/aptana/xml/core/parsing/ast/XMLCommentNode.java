/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.parsing.ast;

/**
 * @author klindsey
 */
public class XMLCommentNode extends XMLNode
{

	private String fText;

	public XMLCommentNode(String text, int start, int end)
	{
		super(XMLNodeType.COMMENT, start, end);
		fText = text;
	}

	@Override
	public String getText()
	{
		return fText;
	}

	@Override
	public String toString()
	{
		return fText;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof XMLCommentNode))
		{
			return false;
		}
		return fText.equals(((XMLCommentNode) obj).fText);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + fText.hashCode();
	}
}
