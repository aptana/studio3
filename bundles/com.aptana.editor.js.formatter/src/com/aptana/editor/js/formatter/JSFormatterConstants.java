/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter;

import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * JavaScript code formatter constants.<br>
 * Since the formatters will be saved in a unified XML file, it's important to have a unique key for every setting. The
 * JavaScript formatter constants are all starting with the {@link #FORMATTER_ID} string.
 */
public interface JSFormatterConstants
{

	/**
	 * JavaScript formatter ID.
	 */
	public static final String FORMATTER_ID = "js.formatter"; //$NON-NLS-1$

	public static final String FORMATTER_TAB_CHAR = FORMATTER_ID + '.' + CodeFormatterConstants.FORMATTER_TAB_CHAR;
	public static final String FORMATTER_TAB_SIZE = FORMATTER_ID + '.' + CodeFormatterConstants.FORMATTER_TAB_SIZE;

	// Wrapping
	public static final String WRAP_COMMENTS = FORMATTER_ID + ".wrap.comments"; //$NON-NLS-1$
	public static final String WRAP_COMMENTS_LENGTH = FORMATTER_ID + ".wrap.comments.length"; //$NON-NLS-1$

	// Indentation
	public static final String INDENT_BLOCKS = FORMATTER_ID + ".indent.blocks"; //$NON-NLS-1$
	public static final String INDENT_FUNCTION_BODY = FORMATTER_ID + ".indent.function.body"; //$NON-NLS-1$
	public static final String INDENT_SWITCH_BODY = FORMATTER_ID + ".indent.switch.body"; //$NON-NLS-1$
	public static final String INDENT_CASE_BODY = FORMATTER_ID + ".indent.case.body"; //$NON-NLS-1$
	public static final String INDENT_GROUP_BODY = FORMATTER_ID + ".indent.group.body"; //$NON-NLS-1$
	public static final String FORMATTER_INDENTATION_SIZE = FORMATTER_ID + '.'
			+ CodeFormatterConstants.FORMATTER_INDENTATION_SIZE;

	// New lines
	public static final String NEW_LINES_BEFORE_ELSE_STATEMENT = FORMATTER_ID + ".newline.before.else"; //$NON-NLS-1$
	public static final String NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT = FORMATTER_ID + ".newline.before.if.in.elseif"; //$NON-NLS-1$
	public static final String NEW_LINES_BEFORE_CATCH_STATEMENT = FORMATTER_ID + ".newline.before.catch"; //$NON-NLS-1$
	public static final String NEW_LINES_BEFORE_FINALLY_STATEMENT = FORMATTER_ID + ".newline.before.finally"; //$NON-NLS-1$
	public static final String NEW_LINES_BEFORE_DO_WHILE_STATEMENT = FORMATTER_ID + ".newline.before.dowhile"; //$NON-NLS-1$
	public static final String NEW_LINES_BEFORE_NAME_VALUE_PAIRS = FORMATTER_ID + ".newline.before.name.value.pairs"; //$NON-NLS-1$
	public static final String NEW_LINES_BETWEEN_VAR_DECLARATIONS = FORMATTER_ID + ".newline.between.var.declarations"; //$NON-NLS-1$

	// Empty lines
	public static final String LINES_AFTER_FUNCTION_DECLARATION = FORMATTER_ID + ".line.after.function.declaration"; //$NON-NLS-1$
	public static final String LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION = FORMATTER_ID
			+ ".line.after.function.declaration.expression"; //$NON-NLS-1$
	public static final String PRESERVED_LINES = FORMATTER_ID + ".line.preserve"; //$NON-NLS-1$

	// Braces position
	public static final String BRACE_POSITION_FUNCTION_DECLARATION = FORMATTER_ID
			+ ".brace.position.function.declaration"; //$NON-NLS-1$
	public static final String BRACE_POSITION_BLOCK = FORMATTER_ID + ".brace.position.blocks"; //$NON-NLS-1$
	public static final String BRACE_POSITION_BLOCK_IN_SWITCH = FORMATTER_ID + ".brace.position.switch.block"; //$NON-NLS-1$
	public static final String BRACE_POSITION_BLOCK_IN_CASE = FORMATTER_ID + ".brace.position.case.block"; //$NON-NLS-1$

	// Spaces
	public static final String SPACES_BEFORE_COMMAS = FORMATTER_ID + ".spaces.before.commas"; //$NON-NLS-1$
	public static final String SPACES_AFTER_COMMAS = FORMATTER_ID + ".spaces.after.commas"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_UNARY_OPERATOR = FORMATTER_ID + ".spaces.before.unary.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_UNARY_OPERATOR = FORMATTER_ID + ".spaces.after.unary.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_KEY_VALUE_OPERATOR = FORMATTER_ID + ".spaces.before.key.value.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_KEY_VALUE_OPERATOR = FORMATTER_ID + ".spaces.after.key.value.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_ASSIGNMENT_OPERATOR = FORMATTER_ID + ".spaces.before.assignment.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_ASSIGNMENT_OPERATOR = FORMATTER_ID + ".spaces.after.assignment.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_RELATIONAL_OPERATORS = FORMATTER_ID + ".spaces.before.relational.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_RELATIONAL_OPERATORS = FORMATTER_ID + ".spaces.after.relational.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_CONCATENATION_OPERATOR = FORMATTER_ID
			+ ".spaces.before.concatenation.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_CONCATENATION_OPERATOR = FORMATTER_ID
			+ ".spaces.after.concatenation.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_CONDITIONAL_OPERATOR = FORMATTER_ID
			+ ".spaces.before.conditional.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_CONDITIONAL_OPERATOR = FORMATTER_ID + ".spaces.after.conditional.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_POSTFIX_OPERATOR = FORMATTER_ID + ".spaces.before.postfix.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_POSTFIX_OPERATOR = FORMATTER_ID + ".spaces.after.postfix.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_PREFIX_OPERATOR = FORMATTER_ID + ".spaces.before.prefix.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_PREFIX_OPERATOR = FORMATTER_ID + ".spaces.after.prefix.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_ARITHMETIC_OPERATOR = FORMATTER_ID + ".spaces.before.arithmetic.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_ARITHMETIC_OPERATOR = FORMATTER_ID + ".spaces.after.arithmetic.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_FOR_SEMICOLON = FORMATTER_ID + ".spaces.before.for.semicolon.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_FOR_SEMICOLON = FORMATTER_ID + ".spaces.after.for.semicolon.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_SEMICOLON = FORMATTER_ID + ".spaces.before.semicolon.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_SEMICOLON = FORMATTER_ID + ".spaces.after.semicolon.operator"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_CASE_COLON_OPERATOR = FORMATTER_ID + ".spaces.before.case.colon.operator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_CASE_COLON_OPERATOR = FORMATTER_ID + ".spaces.after.case.colon.operator"; //$NON-NLS-1$

	// OFF/ON
	public static final String FORMATTER_OFF_ON_ENABLED = FORMATTER_ID + ".formatter.on.off.enabled"; //$NON-NLS-1$
	public static final String FORMATTER_ON = FORMATTER_ID + ".formatter.on"; //$NON-NLS-1$
	public static final String FORMATTER_OFF = FORMATTER_ID + ".formatter.off"; //$NON-NLS-1$
	public static final String DEFAULT_FORMATTER_OFF = "@formatter:off"; //$NON-NLS-1$
	public static final String DEFAULT_FORMATTER_ON = "@formatter:on"; //$NON-NLS-1$

	// Parenthesis
	// @formatter:off
	public static final String SPACES_BEFORE_OPENING_PARENTHESES = FORMATTER_ID + ".spaces.before.parentheses"; //$NON-NLS-1$
	public static final String SPACES_AFTER_OPENING_PARENTHESES = FORMATTER_ID + ".spaces.after.parentheses"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_CLOSING_PARENTHESES = FORMATTER_ID + ".spaces.before.parentheses.closing"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_OPENING_DECLARATION_PARENTHESES = FORMATTER_ID + ".spaces.before.declaration.parentheses.opening"; //$NON-NLS-1$
	public static final String SPACES_AFTER_OPENING_DECLARATION_PARENTHESES = FORMATTER_ID + ".spaces.after.declaration.parentheses.opening"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_CLOSING_DECLARATION_PARENTHESES = FORMATTER_ID + ".spaces.before.declaration.parentheses.closing"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_OPENING_INVOCATION_PARENTHESES = FORMATTER_ID + ".spaces.before.invocation.parentheses.opening"; //$NON-NLS-1$
	public static final String SPACES_AFTER_OPENING_INVOCATION_PARENTHESES = FORMATTER_ID + ".spaces.after.invocation.parentheses.opening"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_CLOSING_INVOCATION_PARENTHESES = FORMATTER_ID + ".spaces.before.invocation.parentheses.closing"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_OPENING_ARRAY_ACCESS_PARENTHESES = FORMATTER_ID + ".spaces.before.array.access.parentheses.opening"; //$NON-NLS-1$
	public static final String SPACES_AFTER_OPENING_ARRAY_ACCESS_PARENTHESES = FORMATTER_ID + ".spaces.after.array.access.parentheses.opening"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_CLOSING_ARRAY_ACCESS_PARENTHESES = FORMATTER_ID + ".spaces.before.array.access.parentheses.closing"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_OPENING_LOOP_PARENTHESES = FORMATTER_ID + ".spaces.before.loop.parentheses.opening"; //$NON-NLS-1$
	public static final String SPACES_AFTER_OPENING_LOOP_PARENTHESES = FORMATTER_ID + ".spaces.after.loop.parentheses.opening"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_CLOSING_LOOP_PARENTHESES = FORMATTER_ID + ".spaces.before.loop.parentheses.closing"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_OPENING_CONDITIONAL_PARENTHESES = FORMATTER_ID + ".spaces.before.conditional.parentheses.opening"; //$NON-NLS-1$
	public static final String SPACES_AFTER_OPENING_CONDITIONAL_PARENTHESES = FORMATTER_ID + ".spaces.after.conditional.parentheses.opening"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_CLOSING_CONDITIONAL_PARENTHESES = FORMATTER_ID + ".spaces.before.conditional.parentheses.closing"; //$NON-NLS-1$
	// @formatter:on
}
