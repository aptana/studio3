/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.NodeTypes.TypeBracket;

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
	private TypeBracket parenthesesType;

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
			boolean hasCommentBeforeClose, TypeBracket type)
	{
		super(document);
		this.asWrapper = asWrapper;
		this.hasCommentBeforeOpen = hasCommentBeforeOpen;
		this.hasCommentBeforeClose = hasCommentBeforeClose;
		this.parenthesesType = type;
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
		this(document, asWrapper, false, false, null);
	}

	/**
	 * Constructs a new FormatterJSParenthesesNode
	 * 
	 * @param document
	 * @param type
	 */
	public FormatterJSParenthesesNode(IFormatterDocument document, TypeBracket type)
	{
		this(document, false, false, false, type);
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
		if (parenthesesType != null)
		{
			switch (parenthesesType)
			{
				case DECLARATION_PARENTHESIS:
					return getInt(JSFormatterConstants.SPACES_BEFORE_OPENING_DECLARATION_PARENTHESES);
				case INVOCATION_PARENTHESIS:
				case ARRAY_PARENTHESIS:
					return getInt(JSFormatterConstants.SPACES_BEFORE_OPENING_INVOCATION_PARENTHESES);
				case ARRAY_SQUARE:
					return getInt(JSFormatterConstants.SPACES_BEFORE_OPENING_ARRAY_ACCESS_PARENTHESES);
				case CONDITIONAL_PARENTHESIS:
					return getInt(JSFormatterConstants.SPACES_BEFORE_OPENING_CONDITIONAL_PARENTHESES);
				case LOOP_PARENTHESIS:
					return getInt(JSFormatterConstants.SPACES_BEFORE_OPENING_LOOP_PARENTHESES);
				default:
					return getInt(JSFormatterConstants.SPACES_BEFORE_OPENING_PARENTHESES);
			}
		}
		return super.getSpacesCountBefore();
	}

	/**
	 * We override the acceptBody to control any spaces that should be added before or after the body.
	 * 
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#acceptBody(com.aptana.formatter.IFormatterContext,
	 *      com.aptana.formatter.IFormatterWriter)
	 */
	@Override
	protected void acceptBody(IFormatterContext context, IFormatterWriter visitor) throws Exception
	{
		int spacesBeforeBody = getSpacesBeforeBody();
		if (spacesBeforeBody > 0)
		{
			writeSpaces(visitor, context, spacesBeforeBody);
		}
		super.acceptBody(context, visitor);
		int spacesAfterBody = getSpacesAfterBody();
		if (spacesAfterBody > 0)
		{
			writeSpaces(visitor, context, spacesAfterBody);
		}
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

	/**
	 * @return The amount of spaces that we should insert before the body.
	 */
	private int getSpacesBeforeBody()
	{
		if (isAsWrapper() || parenthesesType == null)
		{
			return 0;
		}
		switch (parenthesesType)
		{
			case DECLARATION_PARENTHESIS:
				return getInt(JSFormatterConstants.SPACES_AFTER_OPENING_DECLARATION_PARENTHESES);
			case INVOCATION_PARENTHESIS:
			case ARRAY_PARENTHESIS:
				return getInt(JSFormatterConstants.SPACES_AFTER_OPENING_INVOCATION_PARENTHESES);
			case ARRAY_SQUARE:
				return getInt(JSFormatterConstants.SPACES_AFTER_OPENING_ARRAY_ACCESS_PARENTHESES);
			case CONDITIONAL_PARENTHESIS:
				return getInt(JSFormatterConstants.SPACES_AFTER_OPENING_CONDITIONAL_PARENTHESES);
			case LOOP_PARENTHESIS:
				return getInt(JSFormatterConstants.SPACES_AFTER_OPENING_LOOP_PARENTHESES);
			default:
				return getInt(JSFormatterConstants.SPACES_AFTER_OPENING_PARENTHESES);
		}
	}

	/**
	 * @return The amount of spaces that we should insert after the body.
	 */
	private int getSpacesAfterBody()
	{
		if (isAsWrapper() || parenthesesType == null)
		{
			return 0;
		}
		switch (parenthesesType)
		{
			case DECLARATION_PARENTHESIS:
				return getInt(JSFormatterConstants.SPACES_BEFORE_CLOSING_DECLARATION_PARENTHESES);
			case INVOCATION_PARENTHESIS:
			case ARRAY_PARENTHESIS:
				return getInt(JSFormatterConstants.SPACES_BEFORE_CLOSING_INVOCATION_PARENTHESES);
			case ARRAY_SQUARE:
				return getInt(JSFormatterConstants.SPACES_BEFORE_CLOSING_ARRAY_ACCESS_PARENTHESES);
			case CONDITIONAL_PARENTHESIS:
				return getInt(JSFormatterConstants.SPACES_BEFORE_CLOSING_CONDITIONAL_PARENTHESES);
			case LOOP_PARENTHESIS:
				return getInt(JSFormatterConstants.SPACES_BEFORE_CLOSING_LOOP_PARENTHESES);
			default:
				return getInt(JSFormatterConstants.SPACES_BEFORE_CLOSING_PARENTHESES);
		}
	}
}