/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;

/**
 * An implicit block formatter node. An implicit block node has no open and close chars.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSImplicitBlockNode extends FormatterBlockWithBeginEndNode
{
	private boolean consumePrevSpaces;
	private boolean shouldIndent;
	private int spacesCountBefore;

	/**
	 * Constructs a new FormatterJSImplicitBlockNode
	 * 
	 * @param document
	 * @param consumePrevSpaces
	 * @param shouldIndent
	 * @param spacesCountBefore
	 */
	public FormatterJSImplicitBlockNode(IFormatterDocument document, boolean consumePrevSpaces, boolean shouldIndent,
			int spacesCountBefore)
	{
		super(document);
		this.consumePrevSpaces = consumePrevSpaces;
		this.shouldIndent = shouldIndent;
		this.spacesCountBefore = spacesCountBefore;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return shouldIndent;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */
	@Override
	protected boolean isAddingEndNewLine()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
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
		return consumePrevSpaces;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		return spacesCountBefore;
	}

}
