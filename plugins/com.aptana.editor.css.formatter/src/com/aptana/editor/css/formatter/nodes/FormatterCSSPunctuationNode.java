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
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.NodeTypes.TypePunctuation;

public class FormatterCSSPunctuationNode extends FormatterBlockWithBeginNode
{

	private TypePunctuation punctuationType;
	private boolean isLastNodeInDeclaration;

	public FormatterCSSPunctuationNode(IFormatterDocument document, TypePunctuation punctuationType,
			boolean isLastNodeInDeclaration)
	{
		super(document);
		this.punctuationType = punctuationType;
		this.isLastNodeInDeclaration = isLastNodeInDeclaration;

	}

	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return true;
	}

	@Override
	public int getSpacesCountBefore()
	{
		switch (punctuationType)
		{
			case CSS_CHILD_COMBINATOR:
				return getDocument().getInt(CSSFormatterConstants.SPACES_BEFORE_CHILD_COMBINATOR);
			case SELECTOR_COLON:
			case PROPERTY_COLON:
				return getDocument().getInt(CSSFormatterConstants.SPACES_BEFORE_COLON);
			case COMMA:
				return getDocument().getInt(CSSFormatterConstants.SPACES_BEFORE_COMMAS);
			case SEMICOLON:
				return getDocument().getInt(CSSFormatterConstants.SPACES_BEFORE_SEMICOLON);
			default:
				return super.getSpacesCountBefore();
		}
	}

	@Override
	public int getSpacesCountAfter()
	{
		if (isLastNodeInDeclaration)
		{
			return super.getSpacesCountBefore();
		}
		switch (punctuationType)
		{
			case CSS_CHILD_COMBINATOR:
				return getDocument().getInt(CSSFormatterConstants.SPACES_AFTER_CHILD_COMBINATOR);
			case SELECTOR_COLON:
			case PROPERTY_COLON:
				return getDocument().getInt(CSSFormatterConstants.SPACES_AFTER_COLON);
			case COMMA:
				return getDocument().getInt(CSSFormatterConstants.SPACES_AFTER_COMMAS);
			case SEMICOLON:
				return getDocument().getInt(CSSFormatterConstants.SPACES_AFTER_SEMICOLON);
			default:
				return super.getSpacesCountBefore();
		}
	}

	protected int getBlankLinesAfter(IFormatterContext context)
	{
		return isLastNodeInDeclaration ? getInt(CSSFormatterConstants.LINES_AFTER_DECLARATION) : super
				.getBlankLinesAfter(context);
	}

}
