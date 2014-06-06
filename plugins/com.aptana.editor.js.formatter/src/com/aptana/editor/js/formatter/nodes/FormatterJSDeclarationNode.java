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
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.FormatterTextNode;
import com.aptana.formatter.nodes.IFormatterTextNode;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSArgumentsNode;
import com.aptana.js.core.parsing.ast.JSBinaryOperatorNode;
import com.aptana.js.core.parsing.ast.JSVarNode;
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

	@Override
	public void setBegin(IFormatterTextNode begin)
	{
		if (node.getNodeType() == IJSNodeTypes.DECLARATION && isMultipleVarDeclaration()
				&& getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BETWEEN_VAR_DECLARATIONS))
		{
			// Hack the begin text node to increase indent!
			IFormatterTextNode newBegin = new FormatterTextNode(begin.getDocument(), begin.getStartOffset(),
					begin.getEndOffset())
			{
				@Override
				public void accept(IFormatterContext context, IFormatterWriter visitor) throws Exception
				{
					visitor.writeIndent(context); // write the normal indent level
					super.accept(context, visitor);
				}

				@Override
				public boolean shouldConsumePreviousWhiteSpaces()
				{
					return true;
				}

				@Override
				public int getSpacesCountBefore()
				{
					// Add an additional 4 spaces for "var " prefix from first var decl.
					return 4;
				}
			};
			begin = newBegin;
		}
		super.setBegin(begin);
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
		switch (node.getNodeType())
		{
			case IJSNodeTypes.NAME_VALUE_PAIR:
				return node.getParent().getChild(0) == node
						|| getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_NAME_VALUE_PAIRS);
			case IJSNodeTypes.DECLARATION:
				return isMultipleVarDeclaration()
						&& getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BETWEEN_VAR_DECLARATIONS);
		}
		if (isPartOfExpression(node))
		{
			return false;
		}
		switch (node.getNodeType())
		{
			case IJSNodeTypes.CATCH:
				return getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_CATCH_STATEMENT);
			case IJSNodeTypes.FINALLY:
				return !hasBlockedChild
						|| getDocument().getBoolean(JSFormatterConstants.NEW_LINES_BEFORE_FINALLY_STATEMENT);
			case IJSNodeTypes.FUNCTION:
			case IJSNodeTypes.VAR:
				return !isLoopOrExpressionNode(node.getParent());
		}
		return true;
	}

	/**
	 * Determines if this is the child of a JSVarNode, but is not the first child. We have options to force multiple var
	 * declarations to each be on a newline, with indentation matching.
	 * 
	 * @return
	 */
	protected boolean isMultipleVarDeclaration()
	{
		return node.getParent() instanceof JSVarNode && node.getParent().getChild(0) != node;
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
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		// TODO add preferences
		short parentNodeType = node.getParent().getNodeType();
		if (parentNodeType == IJSNodeTypes.GROUP)
		{
			return 0;
		}
		if (parentNodeType == IJSNodeTypes.ARGUMENTS)
		{
			// Set to zero only if it's the first argument
			JSArgumentsNode argumentsNode = (JSArgumentsNode) node.getParent();
			if (argumentsNode.getChild(0) == node)
			{
				return 0;
			}
		}
		short nodeType = node.getNodeType();
		if (nodeType == IJSNodeTypes.VAR)
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
			case IJSNodeTypes.WHILE:
			case IJSNodeTypes.FOR:
			case IJSNodeTypes.FOR_IN:
				return true;
		}
		return isPartOfExpression(iParseNode);
	}
}
