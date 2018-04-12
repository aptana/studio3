/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter;

import com.aptana.formatter.FormatterContext;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterNode;

/**
 * An CSS formatter context.
 */
public class CSSFormatterContext extends FormatterContext
{
	private static final String WRAPPING_COMMENT_PREFIX = " * "; //$NON-NLS-1$

	/**
	 * @param indent
	 */
	public CSSFormatterContext(int indent)
	{
		super(indent);
	}

	/**
	 * Returns true only if the given node is a container node (of type {@link IFormatterContainerNode}).
	 * 
	 * @param node
	 *            An {@link IFormatterNode}
	 * @return True only if the given node is a container node; False, otherwise.
	 * @see com.aptana.formatter.FormatterContext#isCountable(com.aptana.formatter.nodes.IFormatterNode)
	 */
	protected boolean isCountable(IFormatterNode node)
	{
		return node instanceof IFormatterContainerNode;
	}

	/**
	 * Check if the char sequence starts with a '/*' sequence. If so, return the length of the sequence; Otherwise,
	 * return 0.
	 * 
	 * @see com.aptana.formatter.IFormatterContext#getCommentStartLength(CharSequence, int)
	 */
	public int getCommentStartLength(CharSequence chars, int offset)
	{
		if (chars.length() > offset + 1 && chars.charAt(offset) == '/' && chars.charAt(offset + 1) == '*')
		{
			return 2;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#getWrappingCommentPrefix(java.lang.String)
	 */
	public String getWrappingCommentPrefix(String text)
	{
		return WRAPPING_COMMENT_PREFIX;
	}
}