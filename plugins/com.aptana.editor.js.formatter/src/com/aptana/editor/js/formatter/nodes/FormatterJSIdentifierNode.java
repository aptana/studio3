/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.parsing.ast.IParseNode;

/**
 * JS Identifier node.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSIdentifierNode extends FormatterJSTextNode
{

	private JSNode node;

	/**
	 * @param node
	 * @param document
	 * @param hasCommentBefore
	 */
	public FormatterJSIdentifierNode(IFormatterDocument document, JSNode node, boolean hasCommentBefore)
	{
		super(document, !hasCommentBefore);
		this.node = node;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		IParseNode parent = node.getParent();
		boolean isFirstInLine = parent.getStartingOffset() == node.getStartingOffset();
		if (parent instanceof JSNode && ((JSNode) parent).getSemicolonIncluded())
		{
			return isFirstInLine;
		}
		return !shouldConsumePreviousWhiteSpaces();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSTextNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		IParseNode parent = node.getParent();
		if (parent != null)
		{
			boolean isFirstChild = parent.getChild(0) == node;
			short parentType = parent.getNodeType();
			if (isFirstChild)
			{
				switch (parentType)
				{
					case JSNodeTypes.PARAMETERS:
					case JSNodeTypes.ARGUMENTS:
					case JSNodeTypes.ADD:
					case JSNodeTypes.MULTIPLY:
					case JSNodeTypes.MOD:
					case JSNodeTypes.DIVIDE:
					case JSNodeTypes.BITWISE_AND:
					case JSNodeTypes.BITWISE_OR:
					case JSNodeTypes.BITWISE_XOR:
					case JSNodeTypes.PRE_INCREMENT:
					case JSNodeTypes.PRE_DECREMENT:
					case JSNodeTypes.POST_INCREMENT:
					case JSNodeTypes.POST_DECREMENT:
					case JSNodeTypes.NEGATIVE:
					case JSNodeTypes.POSITIVE:
					case JSNodeTypes.EQUAL:
					case JSNodeTypes.GREATER_THAN:
					case JSNodeTypes.GREATER_THAN_OR_EQUAL:
					case JSNodeTypes.IDENTITY:
					case JSNodeTypes.LESS_THAN:
					case JSNodeTypes.LESS_THAN_OR_EQUAL:
					case JSNodeTypes.LOGICAL_AND:
					case JSNodeTypes.LOGICAL_OR:
					case JSNodeTypes.NOT_EQUAL:
					case JSNodeTypes.NOT_IDENTITY:
					case JSNodeTypes.ADD_AND_ASSIGN:
					case JSNodeTypes.BITWISE_AND_AND_ASSIGN:
					case JSNodeTypes.BITWISE_OR_AND_ASSIGN:
					case JSNodeTypes.BITWISE_XOR_AND_ASSIGN:
					case JSNodeTypes.DIVIDE_AND_ASSIGN:
					case JSNodeTypes.MOD_AND_ASSIGN:
					case JSNodeTypes.MULTIPLY_AND_ASSIGN:
					case JSNodeTypes.SHIFT_LEFT_AND_ASSIGN:
					case JSNodeTypes.SHIFT_RIGHT_AND_ASSIGN:
					case JSNodeTypes.SHIFT_RIGHT:
					case JSNodeTypes.SHIFT_LEFT:
					case JSNodeTypes.SUBTRACT_AND_ASSIGN:
					case JSNodeTypes.ARITHMETIC_SHIFT_RIGHT:
					case JSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN:
					case JSNodeTypes.IF:
					case JSNodeTypes.WHILE:
					case JSNodeTypes.COMMA:
					case JSNodeTypes.ELEMENTS:
					case JSNodeTypes.WITH:
					case JSNodeTypes.FOR:
					case JSNodeTypes.FOR_IN:
					case JSNodeTypes.LOGICAL_NOT:
					case JSNodeTypes.BITWISE_NOT:
					case JSNodeTypes.GROUP:
						return 0;
					default:
						return 1;
				}
			}
			else
			{
				// Using switch-case here to prepare for any future tweaks.
				switch (parentType)
				{
					case JSNodeTypes.FOR_IN:
						return 1;
				}
			}
		}
		return 0;
	}
}
