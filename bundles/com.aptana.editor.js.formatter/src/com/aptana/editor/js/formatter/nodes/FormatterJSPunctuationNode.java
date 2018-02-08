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
import com.aptana.formatter.nodes.NodeTypes.TypePunctuation;

/**
 * A JS formatter node for punctuation elements, such as commas, colons etc.<br>
 * A punctuation node is defined, by default, to consume all white spaces in front of it.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSPunctuationNode extends FormatterJSTextNode
{

	private TypePunctuation nodeType;
	private boolean forceLineTermination;

	/**
	 * Constructs a new FormatterJSPunctuationNode.
	 * 
	 * @param document
	 * @param nodeType
	 */
	public FormatterJSPunctuationNode(IFormatterDocument document, TypePunctuation nodeType, boolean hasCommentBefore)
	{
		super(document, true, hasCommentBefore);
		this.nodeType = nodeType;

	}

	/**
	 * Constructs a new FormatterJSPunctuationNode.
	 * 
	 * @param document
	 * @param nodeType
	 * @param forceLineTermination
	 *            - Force this node to terminate with a new line
	 */
	public FormatterJSPunctuationNode(IFormatterDocument document, TypePunctuation nodeType,
			boolean forceLineTermination, boolean hasCommentBefore)
	{
		this(document, nodeType, hasCommentBefore);
		this.forceLineTermination = forceLineTermination;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSTextNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		switch (nodeType)
		{
			case CASE_COLON:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_CASE_COLON_OPERATOR);
			case COMMA:
			case ARRAY_COMMA:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_COMMAS);
			case SEMICOLON:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_SEMICOLON);
			case FOR_SEMICOLON:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_FOR_SEMICOLON);
			default:
				return super.getSpacesCountBefore();
		}

	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSTextNode#getSpacesCountAfter()
	 */
	@Override
	public int getSpacesCountAfter()
	{
		switch (nodeType)
		{
			case CASE_COLON:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_CASE_COLON_OPERATOR);
			case COMMA:
			case ARRAY_COMMA:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_COMMAS);
			case SEMICOLON:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_SEMICOLON);
			case FOR_SEMICOLON:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_FOR_SEMICOLON);
			default:
				return super.getSpacesCountBefore();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */
	@Override
	protected boolean isAddingEndNewLine()
	{
		return (forceLineTermination || super.isAddingEndNewLine());
	}

	protected void setForceLineTermination(boolean force)
	{
		forceLineTermination = force;
	}
}
