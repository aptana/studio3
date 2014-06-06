/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.IFormatterWriter;
import com.aptana.formatter.nodes.FormatterTextNode;
import com.aptana.formatter.nodes.IFormatterTextNode;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSNode;
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

	private boolean isMultipleAssignment()
	{
		IParseNode parent = node.getParent();
		if (parent == null)
		{
			return false;
		}
		// If parent is assignmentNode and grandparent is JSCommaNode, then that is a match too, I think?
		short parentType = parent.getNodeType();
		if (parentType != IJSNodeTypes.ASSIGN)
		{
			return false;
		}

		// Now we need to walk up the tree. If at any point we're not the first child of the parent, then we're not
		// the first decl.
		IParseNode grandParent = parent.getParent();
		while (grandParent != null && grandParent.getNodeType() == IJSNodeTypes.COMMA)
		{
			if (grandParent.getChild(0) != parent)
			{
				return true;
			}
			parent = grandParent;
			grandParent = grandParent.getParent();
		}

		return false;
	}

	private boolean isMultipleVarDeclaration()
	{
		IParseNode parent = node.getParent();
		if (parent == null)
		{
			return false;
		}
		if (parent.getNodeType() != IJSNodeTypes.DECLARATION)
		{
			return false;
		}
		IParseNode grandParent = parent.getParent();
		if (grandParent == null)
		{
			return false;
		}
		if (grandParent.getNodeType() != IJSNodeTypes.VAR)
		{
			return false;
		}
		if (grandParent.getChild(0) == parent)
		{
			return false;
		}
		return true;
	}

	@Override
	public void setBegin(IFormatterTextNode begin)
	{
		if (getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BETWEEN_VAR_DECLARATIONS))
		{
			final boolean isMultipleDecl = isMultipleVarDeclaration();
			if (isMultipleDecl || isMultipleAssignment())
			{
				// Hack the begin text node to increase indent!
				IFormatterTextNode newBegin = new FormatterTextNode(begin.getDocument(), begin.getStartOffset(),
						begin.getEndOffset())
				{
					@Override
					public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
					{
						visitor.ensureLineStarted(context);
						if (isMultipleDecl)
						{
							visitor.writeText(context, "    ", false); //$NON-NLS-1$
						}
						super.accept(context, visitor);
					}
				};
				begin = newBegin;
			}
		}
		super.setBegin(begin);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		if (!shouldConsumePreviousSpaces || isAddingBeginLine)
		{
			return true;
		}
		// If we're in a multiple var declaration, we're not the first var, user has asked for newlines between, and
		// we're assigning a value, add a preceding newline.
		if (getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BETWEEN_VAR_DECLARATIONS)
				&& (isMultipleVarDeclaration() || isMultipleAssignment()))
		{
			return true;
		}
		if (isPartOfExpression(node))
		{
			return false;
		}
		IParseNode parent = node.getParent();
		if (parent == null)
		{
			return false;
		}
		boolean isFirstInLine = parent.getStartingOffset() == node.getStartingOffset();
		if (parent instanceof JSNode && ((JSNode) parent).getSemicolonIncluded())
		{
			return isFirstInLine;
		}
		return false;
	}

	/**
	 * @param node
	 * @return
	 */
	private boolean isPartOfExpression(JSNode jsNode)
	{
		if (jsNode == null)
		{
			return false;
		}
		switch (jsNode.getNodeType())
		{
			case IJSNodeTypes.DECLARATION:
			case IJSNodeTypes.ASSIGN:
			case IJSNodeTypes.RETURN:
			case IJSNodeTypes.INVOKE:
			case IJSNodeTypes.GROUP:
			case IJSNodeTypes.ARGUMENTS:
			case IJSNodeTypes.CONDITIONAL:
			case IJSNodeTypes.NAME_VALUE_PAIR:
			case IJSNodeTypes.GET_PROPERTY:
			case IJSNodeTypes.CONSTRUCT:
				return true;
		}
		return false;
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
					case IJSNodeTypes.PARAMETERS:
					case IJSNodeTypes.ARGUMENTS:
					case IJSNodeTypes.ADD:
					case IJSNodeTypes.SUBTRACT:
					case IJSNodeTypes.MULTIPLY:
					case IJSNodeTypes.MOD:
					case IJSNodeTypes.DIVIDE:
					case IJSNodeTypes.BITWISE_AND:
					case IJSNodeTypes.BITWISE_OR:
					case IJSNodeTypes.BITWISE_XOR:
					case IJSNodeTypes.PRE_INCREMENT:
					case IJSNodeTypes.PRE_DECREMENT:
					case IJSNodeTypes.POST_INCREMENT:
					case IJSNodeTypes.POST_DECREMENT:
					case IJSNodeTypes.NEGATIVE:
					case IJSNodeTypes.POSITIVE:
					case IJSNodeTypes.EQUAL:
					case IJSNodeTypes.GREATER_THAN:
					case IJSNodeTypes.GREATER_THAN_OR_EQUAL:
					case IJSNodeTypes.IDENTITY:
					case IJSNodeTypes.LESS_THAN:
					case IJSNodeTypes.LESS_THAN_OR_EQUAL:
					case IJSNodeTypes.LOGICAL_AND:
					case IJSNodeTypes.LOGICAL_OR:
					case IJSNodeTypes.NOT_EQUAL:
					case IJSNodeTypes.NOT_IDENTITY:
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
					case IJSNodeTypes.IF:
					case IJSNodeTypes.WHILE:
					case IJSNodeTypes.COMMA:
					case IJSNodeTypes.ELEMENTS:
					case IJSNodeTypes.WITH:
					case IJSNodeTypes.FOR:
					case IJSNodeTypes.FOR_IN:
					case IJSNodeTypes.LOGICAL_NOT:
					case IJSNodeTypes.BITWISE_NOT:
					case IJSNodeTypes.GROUP:
					case IJSNodeTypes.GET_PROPERTY:
					case IJSNodeTypes.NAME_VALUE_PAIR:
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
					case IJSNodeTypes.FOR_IN:
						return 1;
				}
			}
		}
		return 0;
	}
}
