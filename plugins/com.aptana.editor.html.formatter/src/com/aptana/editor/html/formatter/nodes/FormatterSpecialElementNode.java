/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.nodes;

import com.aptana.editor.html.formatter.HTMLFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;

/**
 * An HTML formatter node for special HTML element which represents a non-HTML content, such as CSS, JS etc.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterSpecialElementNode extends FormatterDefaultElementNode
{

	/**
	 * @param document
	 * @param element
	 */
	public FormatterSpecialElementNode(IFormatterDocument document, String element)
	{
		super(document, element, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.html.formatter.nodes.FormatterDefaultElementNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#accept(com.aptana.formatter.IFormatterContext,
	 * com.aptana.formatter.IFormatterWriter)
	 */
	@Override
	public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		// We reset the indent before we write the foreign content to avoid any compilation errors later with the
		// foreign language. For example, indenting the source might kill the PHP formatter when a HEREDOC is getting
		// indented.
		int indent = context.getIndent();
		context.resetIndent();
		visitor.write(context, getStartOffset(), getEndOffset());
		context.setIndent(indent);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#getBlankLinesAfter(com.aptana.formatter.IFormatterContext
	 * )
	 */
	protected int getBlankLinesAfter(IFormatterContext context)
	{
		return getInt(HTMLFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#getBlankLinesBefore(com.aptana.formatter.IFormatterContext
	 * )
	 */
	protected int getBlankLinesBefore(IFormatterContext context)
	{
		return getInt(HTMLFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS);
	}
}
