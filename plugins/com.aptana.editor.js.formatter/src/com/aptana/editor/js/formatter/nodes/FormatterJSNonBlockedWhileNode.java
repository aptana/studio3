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
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * A formatter node that represents 'while' statements that end with a semicolon and do not contain an inner block.<br>
 * It's defined as a Begin-End node to hold the condition parts as children.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSNonBlockedWhileNode extends FormatterBlockWithBeginNode
{

	/**
	 * @param document
	 * @param parentNodeType
	 */
	public FormatterJSNonBlockedWhileNode(IFormatterDocument document)
	{
		super(document);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		return getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_DO_WHILE_STATEMENT);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return true;
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
}
