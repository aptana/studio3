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
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * An expression wrapper node for the JS formatter.<br>
 * This node can be used to wrap complex expressions and handle the new-lines settings that appears, or should appear,
 * before that expression.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSExpressionWrapperNode extends FormatterBlockWithBeginEndNode
{

	protected boolean forceNewLine;
	protected JSNode node;

	/**
	 * @param document
	 * @param node
	 * @param forceNewLine
	 */
	public FormatterJSExpressionWrapperNode(IFormatterDocument document, JSNode node, boolean forceNewLine)
	{
		super(document);
		this.node = node;
		this.forceNewLine = forceNewLine;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#shouldConsumePreviousWhiteSpaces()
	 */
	@Override
	public boolean shouldConsumePreviousWhiteSpaces()
	{
		if (forceNewLine)
		{
			return false;
		}
		IParseNode parent = node.getParent();
		short parentType = parent.getNodeType();
		switch (parentType)
		{
			case IJSNodeTypes.DECLARATION:
			case IJSNodeTypes.LOGICAL_AND:
			case IJSNodeTypes.LOGICAL_OR:
			case IJSNodeTypes.ELEMENTS:
			case IJSNodeTypes.IF:
			case IJSNodeTypes.WHILE:
			case IJSNodeTypes.FOR:
			case IJSNodeTypes.FOR_IN:
			case IJSNodeTypes.ADD:
			case IJSNodeTypes.MULTIPLY:
			case IJSNodeTypes.MOD:
			case IJSNodeTypes.DIVIDE:
			case IJSNodeTypes.BITWISE_AND:
			case IJSNodeTypes.BITWISE_OR:
			case IJSNodeTypes.BITWISE_XOR:
			case IJSNodeTypes.PRE_INCREMENT:
			case IJSNodeTypes.PRE_DECREMENT:
			case IJSNodeTypes.NEGATIVE:
			case IJSNodeTypes.POSITIVE:
			case IJSNodeTypes.EQUAL:
			case IJSNodeTypes.GREATER_THAN:
			case IJSNodeTypes.GREATER_THAN_OR_EQUAL:
			case IJSNodeTypes.IDENTITY:
			case IJSNodeTypes.LESS_THAN:
			case IJSNodeTypes.LESS_THAN_OR_EQUAL:
			case IJSNodeTypes.NOT_EQUAL:
			case IJSNodeTypes.NOT_IDENTITY:
			case IJSNodeTypes.IN:
			case IJSNodeTypes.ADD_AND_ASSIGN:
			case IJSNodeTypes.BITWISE_AND_AND_ASSIGN:
			case IJSNodeTypes.BITWISE_OR_AND_ASSIGN:
			case IJSNodeTypes.BITWISE_XOR_AND_ASSIGN:
			case IJSNodeTypes.DIVIDE_AND_ASSIGN:
			case IJSNodeTypes.MOD_AND_ASSIGN:
			case IJSNodeTypes.MULTIPLY_AND_ASSIGN:
			case IJSNodeTypes.SHIFT_LEFT_AND_ASSIGN:
			case IJSNodeTypes.SHIFT_RIGHT_AND_ASSIGN:
			case IJSNodeTypes.SHIFT_RIGHT:
			case IJSNodeTypes.SHIFT_LEFT:
			case IJSNodeTypes.SUBTRACT_AND_ASSIGN:
			case IJSNodeTypes.ARITHMETIC_SHIFT_RIGHT:
			case IJSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN:
			case IJSNodeTypes.NAME_VALUE_PAIR:
			case IJSNodeTypes.RETURN:
			case IJSNodeTypes.THROW:
			case IJSNodeTypes.ARGUMENTS:
			case IJSNodeTypes.SUBTRACT:
			case IJSNodeTypes.GROUP:
			case IJSNodeTypes.GET_ELEMENT:
			case IJSNodeTypes.ASSIGN:
			case IJSNodeTypes.COMMA:
			case IJSNodeTypes.CONDITIONAL:
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		if (forceNewLine)
		{
			return true;
		}
		IParseNode parent = node.getParent();
		boolean isFirstInLine = parent.getStartingOffset() == node.getStartingOffset();
		if (parent instanceof JSNode && ((JSNode) parent).getSemicolonIncluded())
		{
			return isFirstInLine;
		}
		return !shouldConsumePreviousWhiteSpaces();
	}

}
