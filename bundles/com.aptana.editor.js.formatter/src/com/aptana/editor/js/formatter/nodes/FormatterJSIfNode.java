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
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSIfNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * @author Shalom
 */
public class FormatterJSIfNode extends FormatterJSDeclarationNode
{

	private boolean inElseIf;

	/**
	 * @param document
	 * @param hasBlockedChild
	 * @param node
	 */
	public FormatterJSIfNode(IFormatterDocument document, boolean hasBlockedChild, IParseNode node, boolean hasCommentBefore)
	{
		super(document, hasBlockedChild, node, hasCommentBefore);
		// Check if this node is located in the 'false' block of a parent 'if'. In that case, we can say for sure that
		// this 'if' arrives right after an 'else'.
		if (node.getParent().getNodeType() == IJSNodeTypes.IF)
		{
			JSIfNode parentIfNode = (JSIfNode) node.getParent();
			inElseIf = parentIfNode.getFalseBlock() == node;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSDeclarationNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return !hasBlockedChild;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		return inElseIf;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		if (shouldConsumePreviousWhiteSpaces())
		{
			return 1;
		}
		return super.getSpacesCountBefore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSDeclarationNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		if (inElseIf && !hasCommentBefore)
		{
			return getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT);
		}
		return true;
	}
}
