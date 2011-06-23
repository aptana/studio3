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
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

public class FormatterCSSSyntaxNode extends FormatterBlockWithBeginNode
{

	private char syntaxType;

	public FormatterCSSSyntaxNode(IFormatterDocument document, char syntaxType)
	{
		super(document);
		this.syntaxType = syntaxType;

	}

	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return true;
	}

	@Override
	public int getSpacesCountBefore()
	{
		switch (syntaxType)
		{
			case '>':
				return getDocument().getInt(CSSFormatterConstants.SPACES_BEFORE_CHILD_COMBINATOR);
			case '(':
				return getDocument().getInt(CSSFormatterConstants.SPACES_BEFORE_PARENTHESES);
			case ':':
				return getDocument().getInt(CSSFormatterConstants.SPACES_BEFORE_SELECTOR_COLON);
			case ',':
				return getDocument().getInt(CSSFormatterConstants.SPACES_BEFORE_COMMAS);
			case ';':
				return getDocument().getInt(CSSFormatterConstants.SPACES_BEFORE_SEMICOLON);
			default:
				return super.getSpacesCountBefore();
		}
	}

	@Override
	public int getSpacesCountAfter()
	{
		switch (syntaxType)
		{
			case '>':
				return getDocument().getInt(CSSFormatterConstants.SPACES_AFTER_CHILD_COMBINATOR);
			case ')':
				return getDocument().getInt(CSSFormatterConstants.SPACES_AFTER_PARENTHESES);
			case ':':
				return getDocument().getInt(CSSFormatterConstants.SPACES_AFTER_SELECTOR_COLON);
			case ',':
				return getDocument().getInt(CSSFormatterConstants.SPACES_AFTER_COMMAS);
			case ';':
				return getDocument().getInt(CSSFormatterConstants.SPACES_AFTER_SEMICOLON);
			case '(':
			default:
				return super.getSpacesCountBefore();
		}
	}

}
