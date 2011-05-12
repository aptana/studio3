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
import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * Formatter node for Switch block.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSSwitchNode extends FormatterJSBlockNode
{

	/**
	 * @param document
	 */
	public FormatterJSSwitchNode(IFormatterDocument document, boolean commentOnPreviousLine)
	{
		super(document, commentOnPreviousLine);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return getDocument().getBoolean(JSFormatterConstants.INDENT_SWITCH_BODY);
	}

	@Override
	protected boolean isAddingBeginNewLine()
	{
		return (commentOnPreviousLine || CodeFormatterConstants.NEW_LINE.equals(getDocument().getString(
				JSFormatterConstants.BRACE_POSITION_BLOCK_IN_SWITCH)));
	}

}
