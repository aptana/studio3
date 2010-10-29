/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
	public FormatterJSFunctionBodyNode(IFormatterDocument document, boolean functionPartOfExpression)
	{
		super(document);
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
		return CodeFormatterConstants.NEW_LINE.equals(getDocument().getString(
				JSFormatterConstants.BRACE_POSITION_FUNCTION_DECLARATION));
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
