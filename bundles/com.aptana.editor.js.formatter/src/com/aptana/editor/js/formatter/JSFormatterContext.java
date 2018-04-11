/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter;

import com.aptana.core.util.StringUtil;
import com.aptana.formatter.FormatterContext;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterNode;

/**
 * A JavaScript formatter context.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterContext extends FormatterContext
{

	/**
	 * @param indent
	 */
	public JSFormatterContext(int indent)
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
	 * Check if the char sequence starts with a /* sequence, a /** or a // sequence. If so, return the length of the
	 * sequence; Otherwise, return 0.
	 * 
	 * @see com.aptana.formatter.IFormatterContext#getCommentStartLength(CharSequence, int)
	 */
	public int getCommentStartLength(CharSequence chars, int offset)
	{
		int sequenceLength = chars.length();
		if (sequenceLength > offset)
		{
			char c = chars.charAt(offset);
			if (c == '*')
			{
				return 1;
			}
			if (c == '/')
			{
				if (sequenceLength > offset + 1)
				{
					char secondChar = chars.charAt(offset + 1);
					if (secondChar == '/')
					{
						// we have a single line comment
						return 2;
					}
					else if (secondChar == '*')
					{
						// we have a multi-line comment, but we still need to determine its nature
						if (sequenceLength > offset + 2 && chars.charAt(offset + 2) == '*')
						{
							return 3;
						}
						return 2;
					}
				}
			}
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#getWrappingCommentPrefix(java.lang.String)
	 */
	public String getWrappingCommentPrefix(String text)
	{
		if (text != null)
		{
			if (text.startsWith("*") || text.startsWith("/*")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return " * "; //$NON-NLS-1$
			}
			if (text.startsWith("//")) //$NON-NLS-1$
			{
				return "// "; //$NON-NLS-1$
			}
		}
		return StringUtil.EMPTY;
	}
}
