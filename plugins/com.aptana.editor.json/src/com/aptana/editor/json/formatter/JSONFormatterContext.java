/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.formatter;

import com.aptana.core.util.StringUtil;
import com.aptana.formatter.FormatterContext;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterNode;

/**
 * JSONFormatterContext
 */
public class JSONFormatterContext extends FormatterContext
{
	/**
	 * JSONFormatterContext
	 * 
	 * @param indent
	 */
	public JSONFormatterContext(int indent)
	{
		super(indent);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.FormatterContext#isCountable(com.aptana.formatter.nodes.IFormatterNode)
	 */
	protected boolean isCountable(IFormatterNode node)
	{
		return node instanceof IFormatterContainerNode;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#getCommentStartLength(java.lang.CharSequence, int)
	 */
	public int getCommentStartLength(CharSequence chars, int offset)
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#getWrappingCommentPrefix(java.lang.String)
	 */
	public String getWrappingCommentPrefix(String text)
	{
		return StringUtil.EMPTY;
	}
}