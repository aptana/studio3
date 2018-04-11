/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.nodes;

import com.aptana.editor.html.formatter.HTMLFormatterConstants;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterCommentNode;

/**
 * An HTML formatter comment node
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterHTMLCommentNode extends FormatterCommentNode
{

	/**
	 * Constructs a new formatter node for HTML comments
	 * 
	 * @param document
	 * @param startOffset
	 * @param endOffset
	 */
	public FormatterHTMLCommentNode(IFormatterDocument document, int startOffset, int endOffset)
	{
		super(document, startOffset, endOffset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterCommentNode#getWrappingKey()
	 */
	public String getWrappingKey()
	{
		return HTMLFormatterConstants.WRAP_COMMENTS;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterCommentNode#isAddingEndNewLine()
	 */
	@Override
	public boolean isAddingEndNewLine()
	{
		return isAddingBeginNewLine();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterCommentNode#isAddingBeginNewLine()
	 */
	@Override
	public boolean isAddingBeginNewLine()
	{
		return getDocument().getBoolean(HTMLFormatterConstants.PLACE_COMMENTS_IN_SEPARATE_LINES);
	}
}
