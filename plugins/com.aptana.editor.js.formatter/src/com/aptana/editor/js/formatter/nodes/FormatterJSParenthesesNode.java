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
 * A JS node formatter for parentheses, which can be used for any other single char open and close pair, such as
 * brackets etc.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSParenthesesNode extends FormatterBlockWithBeginEndNode
{

	private boolean asWrapper;
	private boolean hasCommentBeforeOpen;
	private boolean hasCommentBeforeClose;

	/**
	 * Constructs a new FormatterJSParenthesesNode
	 * 
	 * @param document
	 * @param asWrapper
	 *            Indicate that these parentheses do not have an open and close brackets, but is acting as a wrapper
	 *            node for an expression that appears inside it.
	 * @param hasCommentBeforeOpen
	 *            Indicate that the open parenthesis has a single-line comment right above it.
	 * @param hasCommentBeforeClose
	 *            Indicate that the close parenthesis has a single-line comment right above it.
	 */
	public FormatterJSParenthesesNode(IFormatterDocument document, boolean asWrapper, boolean hasCommentBeforeOpen,
			boolean hasCommentBeforeClose)
	{
		this(document);
		this.asWrapper = asWrapper;
		this.hasCommentBeforeOpen = hasCommentBeforeOpen;
		this.hasCommentBeforeClose = hasCommentBeforeClose;
	}

	/**
	 * Constructs a new FormatterJSParenthesesNode
	 * 
	 * @param document
	 * @param asWrapper
	 *            Indicate that these parentheses do not have an open and close brackets, but is acting as a wrapper
	 *            node for an expression that appears inside it.
	 */
	public FormatterJSParenthesesNode(IFormatterDocument document, boolean asWrapper)
	{
		this(document, asWrapper, false, false);
	}

	/**
	 * Constructs a new FormatterJSParenthesesNode
	 * 
	 * @param document
	 */
	public FormatterJSParenthesesNode(IFormatterDocument document)
	{
		super(document);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		if (isAsWrapper())
		{
			return 1;
		}
		return getInt(JSFormatterConstants.SPACES_BEFORE_PARENTHESES);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountAfter()
	 */
	@Override
	public int getSpacesCountAfter()
	{
		if (isAsWrapper())
		{
			return 0;
		}
		return getInt(JSFormatterConstants.SPACES_AFTER_PARENTHESES);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return !isAddingBeginNewLine();
	}

	/**
	 * @return the asWrapper
	 */
	public boolean isAsWrapper()
	{
		return asWrapper;
	}

	/**
	 * @param hasCommentBeforeOpen
	 *            Indicate that the open parenthesis has a single-line comment right above it.
	 */
	public void setHasCommentBeforeOpen(boolean hasCommentBeforeOpen)
	{
		this.hasCommentBeforeOpen = hasCommentBeforeOpen;
	}

	/**
	 * @param hasCommentBeforeClose
	 *            Indicate that the close parenthesis has a single-line comment right above it.
	 */
	public void setHasCommentBeforeClose(boolean hasCommentBeforeClose)
	{
		this.hasCommentBeforeClose = hasCommentBeforeClose;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		return hasCommentBeforeOpen;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */
	@Override
	protected boolean isAddingEndNewLine()
	{
		return hasCommentBeforeClose && !asWrapper;
	}
}