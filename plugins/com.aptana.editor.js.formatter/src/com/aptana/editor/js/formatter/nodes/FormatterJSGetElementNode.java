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
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSNode;

/**
 * JS formatter node for 'Get' elements (a[b]).
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSGetElementNode extends FormatterBlockWithBeginNode
{

	private JSNode getNode;
	private boolean hasCommentBefore;

	/**
	 * @param document
	 * @param hasCommentBefore
	 */
	public FormatterJSGetElementNode(IFormatterDocument document, JSNode getNode, boolean hasCommentBefore)
	{
		super(document);
		this.getNode = getNode;
		this.hasCommentBefore = hasCommentBefore;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		if (hasCommentBefore)
		{
			return true;
		}
		if (getNode.getParent() instanceof JSNode)
		{
			JSNode parent = (JSNode) getNode.getParent();
			short parentType = parent.getNodeType();
			if (parentType != IJSNodeTypes.RETURN && parentType != IJSNodeTypes.THROW && parentType != IJSNodeTypes.DELETE
					&& parentType != IJSNodeTypes.VOID && parent.getSemicolonIncluded() && parent.getChild(0) == getNode)
			{
				return true;
			}
		}
		return getNode.getSemicolonIncluded();
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
