/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.nodes;

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.ui.CodeFormatterConstants;

public class FormatterCSSBlockNode extends FormatterBlockWithBeginEndNode
{

	private boolean isDeclaration;

	/**
	 * @param document
	 */
	public FormatterCSSBlockNode(IFormatterDocument document, boolean isDeclarationNode)
	{
		super(document);
		isDeclaration = isDeclarationNode;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	protected boolean isIndenting()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	protected boolean isAddingBeginNewLine()
	{
		return CodeFormatterConstants.NEW_LINE.equals(getDocument().getString(
				CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */

	protected boolean isAddingEndNewLine()
	{
		return true;
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

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode#getBlankLinesAfter(com.aptana.formatter.IFormatterContext
	 * )
	 */
	protected int getBlankLinesAfter(IFormatterContext context)
	{
		int lineAfter;
		if (isDeclaration)
		{
			lineAfter = getInt(CSSFormatterConstants.LINES_AFTER_DECLARATION);
		}
		else
		{
			lineAfter = getInt(CSSFormatterConstants.LINES_AFTER_ELEMENTS);
		}
		if (lineAfter == 0)
		{
			return -1;
		}
		return lineAfter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.IFormatterNode#getSpacesCountBefore()
	 */
	public int getSpacesCountBefore()
	{
		return 1;
	}
}
