/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing.ast;

import com.aptana.editor.html.core.IHTMLConstants;
import com.aptana.parsing.ast.ParseNode;

public class HTMLNode extends ParseNode
{

	private short fType;

	public HTMLNode(short type, int start, int end)
	{
		super();
		fType = type;
		this.setLocation(start, end);
	}

	public HTMLNode(short type, HTMLNode[] children, int start, int end)
	{
		this(type, start, end);
		setChildren(children);
	}

	public String getLanguage()
	{
		return IHTMLConstants.CONTENT_TYPE_HTML;
	}

	public short getNodeType()
	{
		return fType;
	}

	protected void setType(short type)
	{
		fType = type;
	}
}
