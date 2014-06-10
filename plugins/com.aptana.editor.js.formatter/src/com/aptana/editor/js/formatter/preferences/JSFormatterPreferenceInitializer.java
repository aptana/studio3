/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.preferences;

import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_BLOCK;
import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_BLOCK_IN_CASE;
import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_BLOCK_IN_SWITCH;
import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_FUNCTION_DECLARATION;
import static com.aptana.editor.js.formatter.JSFormatterConstants.DEFAULT_FORMATTER_OFF;
import static com.aptana.editor.js.formatter.JSFormatterConstants.DEFAULT_FORMATTER_ON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_INDENTATION_SIZE;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_OFF;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_OFF_ON_ENABLED;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_ON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_TAB_CHAR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_TAB_SIZE;
import static com.aptana.editor.js.formatter.JSFormatterConstants.INDENT_BLOCKS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.INDENT_CASE_BODY;
import static com.aptana.editor.js.formatter.JSFormatterConstants.INDENT_FUNCTION_BODY;
import static com.aptana.editor.js.formatter.JSFormatterConstants.INDENT_GROUP_BODY;
import static com.aptana.editor.js.formatter.JSFormatterConstants.INDENT_SWITCH_BODY;
import static com.aptana.editor.js.formatter.JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION;
import static com.aptana.editor.js.formatter.JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_CATCH_STATEMENT;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_DO_WHILE_STATEMENT;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_ELSE_STATEMENT;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_FINALLY_STATEMENT;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_NAME_VALUE_PAIRS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BETWEEN_VAR_DECLARATIONS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.PRESERVED_LINES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_ARITHMETIC_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_ASSIGNMENT_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_CASE_COLON_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_COMMAS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_CONCATENATION_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_CONDITIONAL_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_FOR_SEMICOLON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_KEY_VALUE_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_ARRAY_ACCESS_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_CONDITIONAL_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_DECLARATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_INVOCATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_LOOP_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_POSTFIX_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_PREFIX_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_RELATIONAL_OPERATORS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_SEMICOLON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_UNARY_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_ARITHMETIC_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_ASSIGNMENT_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CASE_COLON_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_ARRAY_ACCESS_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_CONDITIONAL_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_DECLARATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_INVOCATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_LOOP_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_COMMAS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CONCATENATION_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CONDITIONAL_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_FOR_SEMICOLON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_KEY_VALUE_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_ARRAY_ACCESS_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_CONDITIONAL_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_DECLARATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_INVOCATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_LOOP_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_POSTFIX_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_PREFIX_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_RELATIONAL_OPERATORS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_SEMICOLON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_UNARY_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.WRAP_COMMENTS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.WRAP_COMMENTS_LENGTH;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.formatter.JSFormatterPlugin;
import com.aptana.formatter.IDebugScopes;
import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * JavaScript formatter preference initializer.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterPreferenceInitializer extends AbstractPreferenceInitializer
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences store = EclipseUtil.defaultScope().getNode(JSFormatterPlugin.PLUGIN_ID);

		store.put(FORMATTER_TAB_CHAR, CodeFormatterConstants.EDITOR);
		store.put(FORMATTER_TAB_SIZE,
				Integer.toString(EditorUtil.getSpaceIndentSize(JSPlugin.getDefault().getBundle().getSymbolicName())));
		store.put(FORMATTER_INDENTATION_SIZE, "4"); //$NON-NLS-1$
		store.putBoolean(WRAP_COMMENTS, false);
		store.putInt(WRAP_COMMENTS_LENGTH, 80);
		store.putBoolean(INDENT_BLOCKS, true);
		store.putBoolean(INDENT_FUNCTION_BODY, true);
		store.putBoolean(INDENT_SWITCH_BODY, false);
		store.putBoolean(INDENT_CASE_BODY, true);
		store.putBoolean(INDENT_GROUP_BODY, true);
		store.putBoolean(NEW_LINES_BEFORE_CATCH_STATEMENT, false);
		store.putBoolean(NEW_LINES_BEFORE_FINALLY_STATEMENT, false);
		store.putBoolean(NEW_LINES_BEFORE_ELSE_STATEMENT, false);
		store.putBoolean(NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT, false);
		store.putBoolean(NEW_LINES_BEFORE_DO_WHILE_STATEMENT, false);
		store.putBoolean(NEW_LINES_BEFORE_NAME_VALUE_PAIRS, true);
		store.putBoolean(NEW_LINES_BETWEEN_VAR_DECLARATIONS, true);
		store.putInt(LINES_AFTER_FUNCTION_DECLARATION, 1);
		store.putInt(LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION, 0);
		store.putInt(PRESERVED_LINES, 1);
		store.put(BRACE_POSITION_BLOCK, CodeFormatterConstants.SAME_LINE);
		store.put(BRACE_POSITION_BLOCK_IN_CASE, CodeFormatterConstants.SAME_LINE);
		store.put(BRACE_POSITION_BLOCK_IN_SWITCH, CodeFormatterConstants.SAME_LINE);
		store.put(BRACE_POSITION_FUNCTION_DECLARATION, CodeFormatterConstants.SAME_LINE);
		store.putInt(SPACES_BEFORE_COMMAS, 0);
		store.putInt(SPACES_AFTER_COMMAS, 1);
		store.putInt(SPACES_BEFORE_UNARY_OPERATOR, 0);
		store.putInt(SPACES_AFTER_UNARY_OPERATOR, 0);
		store.putInt(SPACES_BEFORE_KEY_VALUE_OPERATOR, 1);
		store.putInt(SPACES_AFTER_KEY_VALUE_OPERATOR, 1);
		store.putInt(SPACES_BEFORE_ASSIGNMENT_OPERATOR, 1);
		store.putInt(SPACES_AFTER_ASSIGNMENT_OPERATOR, 1);
		store.putInt(SPACES_BEFORE_RELATIONAL_OPERATORS, 1);
		store.putInt(SPACES_AFTER_RELATIONAL_OPERATORS, 1);
		store.putInt(SPACES_BEFORE_CONCATENATION_OPERATOR, 1);
		store.putInt(SPACES_AFTER_CONCATENATION_OPERATOR, 1);
		store.putInt(SPACES_BEFORE_CONDITIONAL_OPERATOR, 1);
		store.putInt(SPACES_AFTER_CONDITIONAL_OPERATOR, 1);
		store.putInt(SPACES_BEFORE_POSTFIX_OPERATOR, 0);
		store.putInt(SPACES_AFTER_POSTFIX_OPERATOR, 0);
		store.putInt(SPACES_BEFORE_PREFIX_OPERATOR, 0);
		store.putInt(SPACES_AFTER_PREFIX_OPERATOR, 0);
		store.putInt(SPACES_BEFORE_ARITHMETIC_OPERATOR, 1);
		store.putInt(SPACES_AFTER_ARITHMETIC_OPERATOR, 1);
		store.putInt(SPACES_BEFORE_FOR_SEMICOLON, 0);
		store.putInt(SPACES_AFTER_FOR_SEMICOLON, 1);
		store.putInt(SPACES_BEFORE_SEMICOLON, 0);
		store.putInt(SPACES_AFTER_SEMICOLON, 1);
		store.putInt(SPACES_BEFORE_CASE_COLON_OPERATOR, 1);
		store.putInt(SPACES_AFTER_CASE_COLON_OPERATOR, 1);
		store.putInt(SPACES_BEFORE_OPENING_PARENTHESES, 0);
		store.putInt(SPACES_AFTER_OPENING_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_CLOSING_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_OPENING_DECLARATION_PARENTHESES, 0);
		store.putInt(SPACES_AFTER_OPENING_DECLARATION_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_CLOSING_DECLARATION_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_OPENING_INVOCATION_PARENTHESES, 0);
		store.putInt(SPACES_AFTER_OPENING_INVOCATION_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_CLOSING_INVOCATION_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_OPENING_ARRAY_ACCESS_PARENTHESES, 0);
		store.putInt(SPACES_AFTER_OPENING_ARRAY_ACCESS_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_CLOSING_ARRAY_ACCESS_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_OPENING_LOOP_PARENTHESES, 1);
		store.putInt(SPACES_AFTER_OPENING_LOOP_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_CLOSING_LOOP_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_OPENING_CONDITIONAL_PARENTHESES, 1);
		store.putInt(SPACES_AFTER_OPENING_CONDITIONAL_PARENTHESES, 0);
		store.putInt(SPACES_BEFORE_CLOSING_CONDITIONAL_PARENTHESES, 0);
		store.putBoolean(FORMATTER_OFF_ON_ENABLED, false);
		store.put(FORMATTER_ON, DEFAULT_FORMATTER_ON);
		store.put(FORMATTER_OFF, DEFAULT_FORMATTER_OFF);

		try
		{
			store.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(JSFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
	}
}
