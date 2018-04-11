/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.formatter.IFormatterDocument;

/**
 * @author Shalom
 */
public class FormatterJSLoopNode extends FormatterJSBlockNode
{

	private boolean hasCurlyBlock;

	/**
	 * @param document
	 */
	public FormatterJSLoopNode(IFormatterDocument document, boolean hasCurlyBlock, boolean commentOnPreviousLine)
	{
		super(document, commentOnPreviousLine);
		this.hasCurlyBlock = hasCurlyBlock;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		// TODO Auto-generated method stub
		return !hasCurlyBlock || super.isAddingBeginNewLine();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		// TODO Auto-generated method stub
		return !hasCurlyBlock || super.isIndenting();
	}

}
