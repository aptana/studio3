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
import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * A JS 'case' formatter node.<br>
 * This node represents a 'case' or 'default' part of a switch-case block.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSCaseNode extends FormatterBlockWithBeginNode
{

	private final boolean hasBlockedChild;

	/**
	 * @param document
	 * @param hasBlockedChild
	 */
	public FormatterJSCaseNode(IFormatterDocument document, boolean hasBlockedChild)
	{
		super(document);
		this.hasBlockedChild = hasBlockedChild;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	protected boolean isAddingBeginNewLine()
	{
		return !hasBlockedChild
				|| CodeFormatterConstants.NEW_LINE.equals(getDocument().getString(
						JSFormatterConstants.BRACE_POSITION_BLOCK_IN_CASE));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */
	protected boolean isAddingEndNewLine()
	{
		return isAddingBeginNewLine();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return !hasBlockedChild && getDocument().getBoolean(JSFormatterConstants.INDENT_CASE_BODY);
	}
}
