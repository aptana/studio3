/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.nodes;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.editor.js.parsing.ast.JSArgumentsNode;
import com.aptana.editor.js.parsing.ast.JSBinaryOperatorNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.formatter.IFormatterDocument;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * A JS declaration formatter node.<br>
 * This node represents a declaration part of a javascript block. It can be a function declaration, an if statement
 * part, a while statement declaration etc. Everything up to the open bracket (if exists) will be in this 'declaration'.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class FormatterJSDeclarationNode extends FormatterBlockWithBeginNode
{

	protected boolean hasBlockedChild;
	protected IParseNode node;
	protected boolean hasCommentBefore;

	/**
	 * @param document
	 * @param hasBlockedChild
	 * @param noNewLine
	 *            Provide a hint flag to block any new line added before this node. Note that this is just a hint which
	 *            can be overwritten by a preference setting.
	 * @param node
	 */
	public FormatterJSDeclarationNode(IFormatterDocument document, boolean hasBlockedChild, IParseNode node,
			boolean hasCommentBefore)
	{
		super(document);
		this.hasBlockedChild = hasBlockedChild;
		this.node = node;
		this.hasCommentBefore = hasCommentBefore;
	}

	/**
	 * For a declaration, when this call returns true, a new line is added <b>before</b> the declaration.
	 * 
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		if (hasCommentBefore)
		{
			return true;
		}
		// To change this behavior, it's recommended to create a designated subclass and override this method to return
		// the value set in the preferences.
		if (node instanceof JSBinaryOperatorNode)
		{
			return false;
		}
		if (isPartOfExpression(node))
		{
			return false;
		}
		switch (node.getNodeType())
		{
			case JSNodeTypes.CATCH:
				return getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_CATCH_STATEMENT);
			case JSNodeTypes.FINALLY:
				return !hasBlockedChild
						|| getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_FINALLY_STATEMENT);
			case JSNodeTypes.FUNCTION:
			case JSNodeTypes.VAR:
				return !isLoopOrExpressionNode(node.getParent());
		}
		return true;
	}

	/**
	 * Returns true id the given node has a type that is part of an expression. This will help us avoid breaking the
	 * line that it is located at and keep the 'declaration' in original expression code.
	 * 
	 * @param node
	 * @return
	 */
	public static boolean isPartOfExpression(IParseNode node)
	{
		if (node == null)
		{
			return false;
		}
		switch (node.getNodeType())
		{
			case JSNodeTypes.DECLARATION:
			case JSNodeTypes.ASSIGN:
			case JSNodeTypes.RETURN:
			case JSNodeTypes.INVOKE:
			case JSNodeTypes.GROUP:
			case JSNodeTypes.ARGUMENTS:
			case JSNodeTypes.CONDITIONAL:
			case JSNodeTypes.NAME_VALUE_PAIR:
			case JSNodeTypes.GET_PROPERTY:
			case JSNodeTypes.CONSTRUCT:
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		// TODO add preferences
		short parentNodeType = node.getParent().getNodeType();
		if (parentNodeType == JSNodeTypes.GROUP)
		{
			return 0;
		}
		if (parentNodeType == JSNodeTypes.ARGUMENTS)
		{
			// Set to zero only if it's the first argument
			JSArgumentsNode argumentsNode = (JSArgumentsNode) node.getParent();
			if (argumentsNode.getChild(0) == node)
			{
				return 0;
			}
		}
		short nodeType = node.getNodeType();
		if (nodeType == JSNodeTypes.VAR)
		{
			if (isLoopOrExpressionNode(node.getParent()))
			{
				return 0;
			}
		}
		return 1;
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
	 * @see com.aptana.formatter.nodes.FormatterBlockNode#isIndenting()
	 */
	@Override
	protected boolean isIndenting()
	{
		return !hasBlockedChild;
	}

	private boolean isLoopOrExpressionNode(IParseNode iParseNode)
	{
		switch (iParseNode.getNodeType())
		{
			case JSNodeTypes.WHILE:
			case JSNodeTypes.FOR:
			case JSNodeTypes.FOR_IN:
				return true;
		}
		return isPartOfExpression(iParseNode);
	}
}
