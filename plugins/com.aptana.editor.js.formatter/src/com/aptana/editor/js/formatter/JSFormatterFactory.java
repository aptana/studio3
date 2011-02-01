/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter;

import java.net.URL;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
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

	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(JSFormatterPlugin.PLUGIN_ID,
			JSFormatterConstants.FORMATTER_ID);

	private static final String FORMATTER_PREVIEW_FILE = "formatterPreview.js"; //$NON-NLS-1$

	private static final String[] KEYS = {
			// TODO - Add more...
			JSFormatterConstants.FORMATTER_INDENTATION_SIZE, JSFormatterConstants.FORMATTER_TAB_CHAR,
			JSFormatterConstants.FORMATTER_TAB_SIZE, JSFormatterConstants.WRAP_COMMENTS,
			JSFormatterConstants.WRAP_COMMENTS_LENGTH, JSFormatterConstants.INDENT_BLOCKS,
			JSFormatterConstants.INDENT_CASE_BODY, JSFormatterConstants.INDENT_SWITCH_BODY,
			JSFormatterConstants.INDENT_FUNCTION_BODY, JSFormatterConstants.INDENT_GROUP_BODY,
			JSFormatterConstants.NEW_LINES_BEFORE_CATCH_STATEMENT,
			JSFormatterConstants.NEW_LINES_BEFORE_DO_WHILE_STATEMENT,
			JSFormatterConstants.NEW_LINES_BEFORE_ELSE_STATEMENT,
			JSFormatterConstants.NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT,
			JSFormatterConstants.NEW_LINES_BEFORE_FINALLY_STATEMENT,
			JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION,
			JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION, JSFormatterConstants.PRESERVED_LINES,
			JSFormatterConstants.BRACE_POSITION_BLOCK, JSFormatterConstants.BRACE_POSITION_BLOCK_IN_CASE,
			JSFormatterConstants.BRACE_POSITION_BLOCK_IN_SWITCH,
			JSFormatterConstants.BRACE_POSITION_FUNCTION_DECLARATION };

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
}
