/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter;

import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_BLOCK;
import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_BLOCK_IN_CASE;
import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_BLOCK_IN_SWITCH;
import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_FUNCTION_DECLARATION;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_ID;
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

import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSSourceConfiguration;
import com.aptana.editor.js.JSSourceViewerConfiguration;
import com.aptana.editor.js.formatter.preferences.JSFormatterModifyDialog;
import com.aptana.formatter.AbstractScriptFormatterFactory;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;

/**
 * HTML formatter factory
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterFactory extends AbstractScriptFormatterFactory
{

	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(JSFormatterPlugin.PLUGIN_ID, FORMATTER_ID);

	private static final String FORMATTER_PREVIEW_FILE = "formatterPreview.js"; //$NON-NLS-1$

	private static final String[] KEYS = { FORMATTER_INDENTATION_SIZE, FORMATTER_TAB_CHAR, FORMATTER_TAB_SIZE,
			WRAP_COMMENTS, WRAP_COMMENTS_LENGTH, INDENT_BLOCKS, INDENT_CASE_BODY, INDENT_SWITCH_BODY,
			INDENT_FUNCTION_BODY, INDENT_GROUP_BODY, NEW_LINES_BEFORE_CATCH_STATEMENT,
			NEW_LINES_BEFORE_DO_WHILE_STATEMENT, NEW_LINES_BEFORE_ELSE_STATEMENT,
			NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT, NEW_LINES_BEFORE_FINALLY_STATEMENT,
			NEW_LINES_BEFORE_NAME_VALUE_PAIRS, NEW_LINES_BETWEEN_VAR_DECLARATIONS,
			LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION, LINES_AFTER_FUNCTION_DECLARATION, PRESERVED_LINES,
			BRACE_POSITION_BLOCK, BRACE_POSITION_BLOCK_IN_CASE, BRACE_POSITION_BLOCK_IN_SWITCH,
			BRACE_POSITION_FUNCTION_DECLARATION, SPACES_BEFORE_COMMAS, SPACES_AFTER_COMMAS,
			SPACES_BEFORE_UNARY_OPERATOR, SPACES_AFTER_UNARY_OPERATOR, SPACES_BEFORE_KEY_VALUE_OPERATOR,
			SPACES_AFTER_KEY_VALUE_OPERATOR, SPACES_BEFORE_ASSIGNMENT_OPERATOR, SPACES_AFTER_ASSIGNMENT_OPERATOR,
			SPACES_BEFORE_RELATIONAL_OPERATORS, SPACES_AFTER_RELATIONAL_OPERATORS,
			SPACES_BEFORE_CONCATENATION_OPERATOR, SPACES_AFTER_CONCATENATION_OPERATOR,
			SPACES_BEFORE_CONDITIONAL_OPERATOR, SPACES_AFTER_CONDITIONAL_OPERATOR, SPACES_BEFORE_POSTFIX_OPERATOR,
			SPACES_AFTER_POSTFIX_OPERATOR, SPACES_BEFORE_PREFIX_OPERATOR, SPACES_AFTER_PREFIX_OPERATOR,
			SPACES_BEFORE_ARITHMETIC_OPERATOR, SPACES_AFTER_ARITHMETIC_OPERATOR, SPACES_BEFORE_FOR_SEMICOLON,
			SPACES_AFTER_FOR_SEMICOLON, SPACES_BEFORE_SEMICOLON, SPACES_AFTER_SEMICOLON,
			SPACES_BEFORE_CASE_COLON_OPERATOR, SPACES_AFTER_CASE_COLON_OPERATOR, SPACES_BEFORE_OPENING_PARENTHESES,
			SPACES_AFTER_OPENING_PARENTHESES, SPACES_BEFORE_CLOSING_PARENTHESES,
			SPACES_BEFORE_OPENING_DECLARATION_PARENTHESES, SPACES_AFTER_OPENING_DECLARATION_PARENTHESES,
			SPACES_BEFORE_CLOSING_DECLARATION_PARENTHESES, SPACES_BEFORE_OPENING_INVOCATION_PARENTHESES,
			SPACES_AFTER_OPENING_INVOCATION_PARENTHESES, SPACES_BEFORE_CLOSING_INVOCATION_PARENTHESES,
			SPACES_BEFORE_OPENING_ARRAY_ACCESS_PARENTHESES, SPACES_AFTER_OPENING_ARRAY_ACCESS_PARENTHESES,
			SPACES_BEFORE_CLOSING_ARRAY_ACCESS_PARENTHESES, SPACES_BEFORE_OPENING_LOOP_PARENTHESES,
			SPACES_AFTER_OPENING_LOOP_PARENTHESES, SPACES_BEFORE_CLOSING_LOOP_PARENTHESES,
			SPACES_BEFORE_OPENING_CONDITIONAL_PARENTHESES, SPACES_AFTER_OPENING_CONDITIONAL_PARENTHESES,
			SPACES_BEFORE_CLOSING_CONDITIONAL_PARENTHESES, FORMATTER_OFF_ON_ENABLED, FORMATTER_ON, FORMATTER_OFF };

	public PreferenceKey[] getPreferenceKeys()
	{
		final PreferenceKey[] result = new PreferenceKey[KEYS.length];
		for (int i = 0; i < KEYS.length; ++i)
		{
			final String key = KEYS[i];
			result[i] = new PreferenceKey(JSFormatterPlugin.PLUGIN_ID, key);
		}
		return result;
	}

	public IScriptFormatter createFormatter(String lineSeparator, Map<String, String> preferences)
	{
		return new JSFormatter(lineSeparator, preferences, getMainContentType());
	}

	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new JSFormatterModifyDialog(dialogOwner, this);
	}

	public SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		return new JSSourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	public PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER_PREF_KEY;
	}

	public IPreferenceStore getPreferenceStore()
	{
		return JSFormatterPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPartitioningConfiguration()
	 */
	public Object getPartitioningConfiguration()
	{
		return JSSourceConfiguration.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getEclipsePreferences()
	 */
	@Override
	protected IEclipsePreferences getEclipsePreferences()
	{
		return EclipseUtil.instanceScope().getNode(JSPlugin.PLUGIN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getFormatterTabPolicy()
	 */
	@Override
	protected String getFormatterTabPolicy(Map<String, String> preferences)
	{
		return preferences.get(JSFormatterConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getEditorTabSize()
	 */
	@Override
	protected int getEditorTabSize()
	{
		return EditorUtil.getSpaceIndentSize(JSPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getDefaultEditorTabSize()
	 */
	protected int getDefaultEditorTabSize()
	{
		return EditorUtil.getDefaultSpaceIndentSize(JSPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getFormatterTabSizeKey()
	 */
	@Override
	protected String getFormatterTabSizeKey()
	{
		return JSFormatterConstants.FORMATTER_TAB_SIZE;
	}
}
