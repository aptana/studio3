/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.formatter.IFormatterDocument;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSObjectNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * A JavaScript formatter node for Object blocks (such as hashes etc.)
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSObjectNode extends FormatterJSBlockNode
{

	private JSObjectNode node;

	/**
	 * @param document
	 * @param commentOnPreviousLine
	 * @param hasNameValuePairs
	 */
	public FormatterJSObjectNode(IFormatterDocument document, JSObjectNode node, boolean commentOnPreviousLine)
	{
		super(document, commentOnPreviousLine);
		this.node = node;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSBlockNode#getSpacesCountBefore()
	 */
	public int getSpacesCountBefore()
	{
		// Check for the parent to decide if we need to add any space before the curly-open
		IParseNode parent = node.getParent();
		switch (parent.getNodeType())
		{
			case IJSNodeTypes.ELEMENTS:
				return 0;
			case IJSNodeTypes.ARGUMENTS:
				// Only if it's the first argument, return 0
				if (parent.getChildIndex(node) == 0)
				{
					return 0;
				}
		}
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingEndNewLine()
	 */
	@Override
	protected boolean isAddingEndNewLine()
	{
		return node.getChildCount() > 0;
	}
}
