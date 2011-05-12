/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;

/**
 * A formatter node that marks an 'if' block that arrives right after an 'else' ("...else if...")
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSElseIfNode extends FormatterBlockWithBeginEndNode
{

	private final boolean commentOnPreviousLine;

	/**
	 * @param document
	 * @param commentOnPreviousLine
	 * @param hasBlockedChild
	 * @param isInAssignment
	 */
	public FormatterJSElseIfNode(IFormatterDocument document, boolean commentOnPreviousLine)
	{
		super(document);
		this.commentOnPreviousLine = commentOnPreviousLine;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		// breaking else-if expressions?
		return commentOnPreviousLine
				|| getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */
	@Override
	protected boolean isAddingEndNewLine()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode# shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return !isAddingBeginNewLine();
	}

}
