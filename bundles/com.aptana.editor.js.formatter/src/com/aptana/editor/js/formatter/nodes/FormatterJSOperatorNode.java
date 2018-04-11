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
import com.aptana.formatter.nodes.NodeTypes.TypeOperator;

/**
 * A JS formatter node for operator elements, such as assignments, arrows etc.<br>
 * An operator node is defined, by default, to consume all white spaces in front of it.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class FormatterJSOperatorNode extends FormatterJSTextNode
{
	private final TypeOperator nodeType;
	private boolean isUnary;

	/**
	 * Constructs a new FormatterJSOperatorNode.
	 * 
	 * @param document
	 * @param hasCommentBefore
	 */
	public FormatterJSOperatorNode(IFormatterDocument document, TypeOperator nodeType, boolean isUnary,
			boolean hasCommentBefore)
	{
		super(document, true, hasCommentBefore);
		this.nodeType = nodeType;
		this.isUnary = isUnary;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.formatter.nodes.FormatterJSTextNode#isAddingBeginNewLine()
	 */
	@Override
	protected boolean isAddingBeginNewLine()
	{
		return nodeType == TypeOperator.DELETE || nodeType == TypeOperator.VOID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.php.formatter.nodes.FormatterPHPTextNode#getSpacesCountBefore()
	 */
	@Override
	public int getSpacesCountBefore()
	{
		if (isUnary)
		{
			switch (nodeType)
			{
				case DELETE:
				case TYPEOF:
				case VOID:
					// We need at least one space for the literal unary operators
					return Math.max(1, getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_UNARY_OPERATOR));
			}
			return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_UNARY_OPERATOR);
		}
		switch (nodeType)
		{
			case KEY_VALUE_COLON:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_KEY_VALUE_OPERATOR);
			case ASSIGNMENT:
			case DOT_EQUAL:
			case PLUS_EQUAL:
			case MINUS_EQUAL:
			case MULTIPLY_EQUAL:
			case MODULUS_EQUAL:
			case DIVIDE_EQUAL:
			case OR_EQUAL:
			case AND_EQUAL:
			case XOR_EQUAL:
			case TILDE_EQUAL:
			case SHIFT_LEFT:
			case SHIFT_LEFT_ASSIGN:
			case SHIFT_RIGHT:
			case SHIFT_RIGHT_ASSIGN:
			case SHIFT_RIGHT_ZERO_FILL:
			case SHIFT_RIGHT_ZERO_FILL_ASSIGN:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_ASSIGNMENT_OPERATOR);
			case EQUAL:
			case IDENTICAL:
			case NOT_EQUAL:
			case NOT_EQUAL_ALTERNATE:
			case NOT_IDENTICAL:
			case GREATER_THAN:
			case LESS_THAN:
			case GREATER_THAN_OR_EQUAL:
			case LESS_THAN_OR_EQUAL:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_RELATIONAL_OPERATORS);
			case INSTANCOF:
			case IN:
				// We need at least one space for the 'instanceof' and 'in' operators
				return Math.max(1, getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_RELATIONAL_OPERATORS));
			case PLUS_CONCATENATION:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_CONCATENATION_OPERATOR);
			case CONDITIONAL:
			case CONDITIONAL_COLON:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_CONDITIONAL_OPERATOR);
			case POSTFIX_DECREMENT:
			case POSTFIX_INCREMENT:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_POSTFIX_OPERATOR);
			case PREFIX_DECREMENT:
			case PREFIX_INCREMENT:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_PREFIX_OPERATOR);
			case MULTIPLY:
			case PLUS:
			case MINUS:
			case DIVIDE:
			case MODULUS:
			case XOR:
			case BINARY_AND:
			case BINARY_OR:
			case AND:
			case OR:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_ARITHMETIC_OPERATOR);
			case OR_LITERAL:
			case AND_LITERAL:
			case XOR_LITERAL:
				// We need at least one space for the literal boolean operators
				return Math.max(1, getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_ARITHMETIC_OPERATOR));
			case TILDE:
			case NOT:
				return getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_UNARY_OPERATOR);
			default:
				return super.getSpacesCountBefore();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.nodes.AbstractFormatterNode#getSpacesCountAfter()
	 */
	@Override
	public int getSpacesCountAfter()
	{
		if (isUnary)
		{
			switch (nodeType)
			{
				case DELETE:
				case TYPEOF:
				case VOID:
					// We need at least one space for the literal unary operators
					return Math.max(1, getDocument().getInt(JSFormatterConstants.SPACES_BEFORE_UNARY_OPERATOR));
			}
			return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_UNARY_OPERATOR);
		}
		switch (nodeType)
		{
			case KEY_VALUE_COLON:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_KEY_VALUE_OPERATOR);
			case ASSIGNMENT:
			case DOT_EQUAL:
			case PLUS_EQUAL:
			case MINUS_EQUAL:
			case MULTIPLY_EQUAL:
			case MODULUS_EQUAL:
			case DIVIDE_EQUAL:
			case OR_EQUAL:
			case AND_EQUAL:
			case TILDE_EQUAL:
			case SHIFT_LEFT:
			case SHIFT_LEFT_ASSIGN:
			case SHIFT_RIGHT:
			case SHIFT_RIGHT_ASSIGN:
			case SHIFT_RIGHT_ZERO_FILL:
			case SHIFT_RIGHT_ZERO_FILL_ASSIGN:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_ASSIGNMENT_OPERATOR);
			case EQUAL:
			case IDENTICAL:
			case NOT_EQUAL:
			case NOT_EQUAL_ALTERNATE:
			case NOT_IDENTICAL:
			case GREATER_THAN:
			case LESS_THAN:
			case GREATER_THAN_OR_EQUAL:
			case LESS_THAN_OR_EQUAL:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_RELATIONAL_OPERATORS);
			case INSTANCOF:
			case IN:
				// We need at least one space for the 'instanceof' and 'in' operators
				return Math.max(1, getDocument().getInt(JSFormatterConstants.SPACES_AFTER_RELATIONAL_OPERATORS));
			case PLUS_CONCATENATION:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_CONCATENATION_OPERATOR);
			case CONDITIONAL:
			case CONDITIONAL_COLON:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_CONDITIONAL_OPERATOR);
			case POSTFIX_DECREMENT:
			case POSTFIX_INCREMENT:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_POSTFIX_OPERATOR);
			case PREFIX_DECREMENT:
			case PREFIX_INCREMENT:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_PREFIX_OPERATOR);
			case MULTIPLY:
			case PLUS:
			case MINUS:
			case DIVIDE:
			case MODULUS:
			case XOR:
			case BINARY_AND:
			case BINARY_OR:
			case AND:
			case OR:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_ARITHMETIC_OPERATOR);
			case OR_LITERAL:
			case AND_LITERAL:
			case XOR_LITERAL:
				// We need at least one space for the literal boolean operators
				return Math.max(1, getDocument().getInt(JSFormatterConstants.SPACES_AFTER_ARITHMETIC_OPERATOR));
			case TILDE:
			case NOT:
				return getDocument().getInt(JSFormatterConstants.SPACES_AFTER_UNARY_OPERATOR);
			case DELETE:
			case TYPEOF:
			case VOID:
				// We need at least one space for the literal unary operators
				return Math.max(1, getDocument().getInt(JSFormatterConstants.SPACES_AFTER_UNARY_OPERATOR));
			default:
				return super.getSpacesCountBefore();
		}
	}
}
