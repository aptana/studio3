/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.editor.ruby.formatter.internal.nodes;

import com.aptana.editor.ruby.formatter.RubyFormatterConstants;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterCommentNode;

/**
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterRubyCommentNode extends FormatterCommentNode
{

	/**
	 * @param document
	 * @param startOffset
	 * @param endOffset
	 */
	public FormatterRubyCommentNode(IFormatterDocument document, int startOffset, int endOffset)
	{
		super(document, startOffset, endOffset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterCommentNode#getWrappingKey()
	 */
	@Override
	public String getWrappingKey()
	{
		return RubyFormatterConstants.WRAP_COMMENTS;
	}
}
