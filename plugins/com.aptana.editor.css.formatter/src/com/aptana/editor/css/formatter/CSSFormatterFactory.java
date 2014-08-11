/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter;

import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.CSSSourceConfiguration;
import com.aptana.editor.css.CSSSourceViewerConfiguration;
import com.aptana.editor.css.formatter.preferences.CSSFormatterModifyDialog;
import com.aptana.formatter.AbstractScriptFormatterFactory;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;

public class CSSFormatterFactory extends AbstractScriptFormatterFactory
{

	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(CSSFormatterPlugin.PLUGIN_ID,
			CSSFormatterConstants.FORMATTER_ID);

	private static final String FORMATTER_PREVIEW_FILE = "formatterPreview.css"; //$NON-NLS-1$

	private static final String[] KEYS = {
			// TODO - Add more...
			CSSFormatterConstants.FORMATTER_INDENTATION_SIZE, CSSFormatterConstants.FORMATTER_TAB_CHAR,
			CSSFormatterConstants.FORMATTER_TAB_SIZE, CSSFormatterConstants.WRAP_COMMENTS,
			CSSFormatterConstants.WRAP_COMMENTS_LENGTH, CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS,
			CSSFormatterConstants.LINES_AFTER_ELEMENTS, CSSFormatterConstants.PRESERVED_LINES,
			CSSFormatterConstants.LINES_AFTER_DECLARATION, CSSFormatterConstants.SPACES_AFTER_CHILD_COMBINATOR,
			CSSFormatterConstants.SPACES_AFTER_COMMAS, CSSFormatterConstants.SPACES_AFTER_PARENTHESES,
			CSSFormatterConstants.SPACES_AFTER_COLON, CSSFormatterConstants.SPACES_AFTER_SEMICOLON,
			CSSFormatterConstants.SPACES_BEFORE_CHILD_COMBINATOR, CSSFormatterConstants.SPACES_BEFORE_COMMAS,
			CSSFormatterConstants.SPACES_BEFORE_PARENTHESES, CSSFormatterConstants.SPACES_BEFORE_COLON,
			CSSFormatterConstants.SPACES_BEFORE_SEMICOLON, CSSFormatterConstants.FORMATTER_OFF_ON_ENABLED,
			CSSFormatterConstants.FORMATTER_ON, CSSFormatterConstants.FORMATTER_OFF };

	public PreferenceKey[] getPreferenceKeys()
	{
		final PreferenceKey[] result = new PreferenceKey[KEYS.length];
		for (int i = 0; i < KEYS.length; ++i)
		{
			final String key = KEYS[i];
			result[i] = new PreferenceKey(CSSFormatterPlugin.PLUGIN_ID, key);
		}
		return result;
	}

	public IScriptFormatter createFormatter(String lineSeparator, Map<String, String> preferences)
	{
		return new CSSFormatter(lineSeparator, preferences, getMainContentType());
	}

	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new CSSFormatterModifyDialog(dialogOwner, this);
	}

	public SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		return new CSSSourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	public PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER_PREF_KEY;
	}

	public IPreferenceStore getPreferenceStore()
	{
		return CSSFormatterPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPartitioningConfiguration()
	 */
	public Object getPartitioningConfiguration()
	{
		return CSSSourceConfiguration.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getEclipsePreferences()
	 */
	@Override
	protected IEclipsePreferences getEclipsePreferences()
	{
		return InstanceScope.INSTANCE.getNode(CSSPlugin.PLUGIN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getFormatterTabPolicy()
	 */
	@Override
	protected String getFormatterTabPolicy(Map<String, String> preferences)
	{
		return preferences.get(CSSFormatterConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getEditorTabSize()
	 */
	@Override
	protected int getEditorTabSize()
	{
		return EditorUtil.getSpaceIndentSize(CSSPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getDefaultEditorTabSize()
	 */
	protected int getDefaultEditorTabSize()
	{
		return EditorUtil.getDefaultSpaceIndentSize(CSSPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getFormatterTabSizeKey()
	 */
	@Override
	protected String getFormatterTabSizeKey()
	{
		return CSSFormatterConstants.FORMATTER_TAB_SIZE;
	}
}
