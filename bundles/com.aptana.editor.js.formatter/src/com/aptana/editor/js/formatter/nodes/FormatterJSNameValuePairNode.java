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
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.js.core.parsing.ast.JSNode;

/**
 * A JS formatter node for a name-value pair in a JS Object-node.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSNameValuePairNode extends FormatterBlockWithBeginNode
{

	private JSNode nameValueNode;
	private boolean hasCommentBefore;

	/**
	 * @param document
	 * @param hasCommentBefore
	 */
	public FormatterJSNameValuePairNode(IFormatterDocument document, JSNode nameValueNode, boolean hasCommentBefore)
	{
		super(document);
		this.nameValueNode = nameValueNode;
		this.hasCommentBefore = hasCommentBefore;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		boolean isFirstChild = nameValueNode.getParent().getChild(0) == nameValueNode;
		return isFirstChild || hasCommentBefore
				|| getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_NAME_VALUE_PAIRS);
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
}
