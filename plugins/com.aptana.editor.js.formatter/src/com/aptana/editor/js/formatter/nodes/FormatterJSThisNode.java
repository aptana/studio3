/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.editor.js.parsing.ast.JSThisNode;
import com.aptana.formatter.FormatterDocument;

/**
 * A JS formatter node for the special 'this' keyword.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSThisNode extends FormatterJSTextNode
{

	private JSThisNode thisNode;
	private boolean hasCommentBefore;

	/**
	 * @param document
	 * @param node
	 * @param hasCommentBefore
	 */
	public FormatterJSThisNode(FormatterDocument document, JSThisNode node, boolean hasCommentBefore)
	{
		super(document);
		this.thisNode = node;
		this.hasCommentBefore = hasCommentBefore;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSTextNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		if (hasCommentBefore)
		{
			return true;
		}
		// Add a begin new line in case the 'this' is 
		return super.isAddingBeginNewLine();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSTextNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		// TODO Auto-generated method stub
		return super.getSpacesCountBefore();
	}

}
