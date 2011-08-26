/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing.ast;

public class HTMLTextNode extends HTMLNode
{

	private String fText;

	public HTMLTextNode(String text, int start, int end)
	{
		super(IHTMLNodeTypes.TEXT, start, end);
		fText = text;
	}

	public void setText(String text)
	{
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
}
