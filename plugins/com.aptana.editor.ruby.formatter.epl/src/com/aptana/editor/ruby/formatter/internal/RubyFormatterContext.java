/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.editor.ruby.formatter.internal;

import com.aptana.editor.ruby.formatter.internal.nodes.FormatterRequireNode;
import com.aptana.formatter.FormatterContext;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterNode;

public class RubyFormatterContext extends FormatterContext
{

	public RubyFormatterContext(int indent)
	{
		super(indent);
	}

	protected boolean isCountable(IFormatterNode node)
	{
		return node instanceof IFormatterContainerNode || node instanceof FormatterRequireNode;
	}

	/**
	 * Check if the char sequence starts with a '#' sign. If so, return 1. Otherwise, return 0.
	 * 
	 * @see com.aptana.formatter.IFormatterContext#getCommentStartLength(CharSequence, int)
	 */
	public int getCommentStartLength(CharSequence chars, int offset)
	{
		if (chars.length() > offset && chars.charAt(offset) == '#')
		{
			return 1;
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterContext#getWrappingCommentPrefix(java.lang.String)
	 */
	public String getWrappingCommentPrefix(String text)
	{
		return "# "; //$NON-NLS-1$
	}

}
