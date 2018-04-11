/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * A JS function body formatter node.<br>
 * This node represents the body part of the function (everything between the curly-brackets).
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSFunctionBodyNode extends FormatterJSBlockNode
{

	private boolean functionPartOfExpression;

	/**
	 * @param document
	 * @param functionPartOfExpression
	 */
	public FormatterJSFunctionBodyNode(IFormatterDocument document, boolean functionPartOfExpression,
			boolean commentOnPreviousLine)
	{
		super(document, commentOnPreviousLine);
		this.functionPartOfExpression = functionPartOfExpression;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		// adds a new line before the start curly bracket
		return (commentOnPreviousLine || CodeFormatterConstants.NEW_LINE.equals(getDocument().getString(
				JSFormatterConstants.BRACE_POSITION_FUNCTION_DECLARATION)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return getDocument().getBoolean(JSFormatterConstants.INDENT_FUNCTION_BODY);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSBlockNode#shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return !isAddingBeginNewLine();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#getBlankLinesAfter(com.aptana.formatter.IFormatterContext
	 * )
	 */
	@Override
	protected int getBlankLinesAfter(IFormatterContext context)
	{
		if (functionPartOfExpression)
		{
			return getDocument().getInt(JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION);
		}
		return getDocument().getInt(JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION);
	}
}
