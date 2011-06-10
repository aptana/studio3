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
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;

/**
 * A function invocation formatter node.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSFunctionInvocationNode extends FormatterBlockWithBeginNode
{

	private JSNode invocationNode;
	private boolean hasCommentBefore;

	/**
	 * @param document
	 * @param invocationNode
	 * @param hasSemicolon
	 */
	public FormatterJSFunctionInvocationNode(IFormatterDocument document, JSNode invocationNode,
			boolean hasCommentBefore)
	{
		super(document);
		this.invocationNode = invocationNode;
		this.hasCommentBefore = hasCommentBefore;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		switch (invocationNode.getParent().getNodeType())
		{
			case JSNodeTypes.ASSIGN:
			case JSNodeTypes.DECLARATION:
			case JSNodeTypes.CONDITIONAL:
			case JSNodeTypes.LOGICAL_AND:
			case JSNodeTypes.LOGICAL_OR:
			case JSNodeTypes.ELEMENTS:
			case JSNodeTypes.IF:
			case JSNodeTypes.WHILE:
			case JSNodeTypes.FOR:
			case JSNodeTypes.FOR_IN:
			case JSNodeTypes.ADD:
			case JSNodeTypes.MULTIPLY:
			case JSNodeTypes.MOD:
			case JSNodeTypes.DIVIDE:
			case JSNodeTypes.BITWISE_AND:
			case JSNodeTypes.BITWISE_OR:
			case JSNodeTypes.BITWISE_XOR:
			case JSNodeTypes.PRE_INCREMENT:
			case JSNodeTypes.PRE_DECREMENT:
			case JSNodeTypes.NEGATIVE:
			case JSNodeTypes.POSITIVE:
			case JSNodeTypes.EQUAL:
			case JSNodeTypes.GREATER_THAN:
			case JSNodeTypes.GREATER_THAN_OR_EQUAL:
			case JSNodeTypes.IDENTITY:
			case JSNodeTypes.LESS_THAN:
			case JSNodeTypes.LESS_THAN_OR_EQUAL:
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
			case JSNodeTypes.NAME_VALUE_PAIR:
			case JSNodeTypes.COMMA:
			case JSNodeTypes.RETURN:
			case JSNodeTypes.THROW:
			case JSNodeTypes.ARGUMENTS:
				return true;
			default:
				return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		return hasCommentBefore;
	}

}
