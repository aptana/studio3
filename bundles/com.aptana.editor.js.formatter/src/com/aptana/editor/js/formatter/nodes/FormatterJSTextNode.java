/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * A generic JS text node formatter.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSTextNode extends FormatterBlockWithBeginNode
{

	protected boolean shouldConsumePreviousSpaces;
	protected boolean isAddingBeginLine;
	private int spacesCountBefore;
	private int spacesCountAfter;

	/**
	 * @param document
	 */
	public FormatterJSTextNode(IFormatterDocument document)
	{
		super(document);
	}

	/**
	 * @param document
	 * @param shouldConsumePreviousSpaces
	 */
	public FormatterJSTextNode(IFormatterDocument document, boolean shouldConsumePreviousSpaces)
	{
		this(document, shouldConsumePreviousSpaces, false);
	}

	/**
	 * @param document
	 * @param shouldConsumePreviousSpaces
	 * @param isAddingBeginLine
	 */
	public FormatterJSTextNode(IFormatterDocument document, boolean shouldConsumePreviousSpaces,
			boolean isAddingBeginLine)
	{
		this(document);
		this.shouldConsumePreviousSpaces = shouldConsumePreviousSpaces;
		this.isAddingBeginLine = isAddingBeginLine;
	}

	/**
	 * @param document
	 * @param shouldConsumePreviousSpaces
	 * @param spacesCountBefore
	 * @param isAddingBeginLine
	 */
	public FormatterJSTextNode(IFormatterDocument document, boolean shouldConsumePreviousSpaces, int spacesCountBefore,
			int spacesCountAfter, boolean isAddingBeginLine)
	{
		this(document, shouldConsumePreviousSpaces, isAddingBeginLine);
		this.spacesCountBefore = spacesCountBefore;
		this.spacesCountAfter = spacesCountAfter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return !isAddingBeginLine && shouldConsumePreviousSpaces;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		return isAddingBeginLine;
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountAfter()
	 */
	@Override
	public int getSpacesCountAfter()
	{
		return spacesCountAfter;
	}

}
