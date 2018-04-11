/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.nodes;

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;

public class FormatterCSSParenthesesNode extends FormatterBlockWithBeginEndNode
{

	public FormatterCSSParenthesesNode(IFormatterDocument document)
	{
		super(document);
	}

	@Override
	public int getSpacesCountBefore()
	{
		return getInt(CSSFormatterConstants.SPACES_BEFORE_PARENTHESES);
	}

	@Override
	public int getSpacesCountAfter()
	{
		return getInt(CSSFormatterConstants.SPACES_AFTER_PARENTHESES);
	}

	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return true;
	}

}
