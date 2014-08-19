/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.formatter;

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
import com.aptana.editor.json.JSONPlugin;
import com.aptana.editor.json.JSONSourceConfiguration;
import com.aptana.editor.json.JSONSourceViewerConfiguration;
import com.aptana.editor.json.preferences.IPreferenceConstants;
import com.aptana.editor.json.preferences.JSONFormatterModifyDialog;
import com.aptana.formatter.AbstractScriptFormatterFactory;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;

public class JSONFormatterFactory extends AbstractScriptFormatterFactory
{
	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(JSONPlugin.PLUGIN_ID,
			IPreferenceConstants.FORMATTER_ID);
	private static final String FORMATTER_PREVIEW_FILE = "formatting-preview.json"; //$NON-NLS-1$
	private static final PreferenceKey[] KEYS = { //
	new PreferenceKey(JSONPlugin.PLUGIN_ID, IPreferenceConstants.FORMATTER_INDENTATION_SIZE), //
			new PreferenceKey(JSONPlugin.PLUGIN_ID, IPreferenceConstants.FORMATTER_TAB_CHAR), //
			new PreferenceKey(JSONPlugin.PLUGIN_ID, IPreferenceConstants.FORMATTER_TAB_SIZE) //
	};

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.IScriptFormatterFactory#createDialog(com.aptana.formatter.ui.IFormatterModifyDialogOwner)
	 */
	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new JSONFormatterModifyDialog(dialogOwner, this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#createFormatter(java.lang.String, java.util.Map)
	 */
	public IScriptFormatter createFormatter(String lineSeparator, Map<String, String> preferences)
	{
		return new JSONFormatter(lineSeparator, preferences, getMainContentType());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.IScriptFormatterFactory#createSimpleSourceViewerConfiguration(org.eclipse.jface.text.source
	 * .ISharedTextColors, org.eclipse.jface.preference.IPreferenceStore, org.eclipse.ui.texteditor.ITextEditor,
	 * boolean)
	 */
	public SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		return new JSONSourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getFormatterPreferenceKey()
	 */
	public PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER_PREF_KEY;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPreferenceKeys()
	 */
	public PreferenceKey[] getPreferenceKeys()
	{
		return KEYS;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPreferenceStore()
	 */
	public IPreferenceStore getPreferenceStore()
	{
		return JSONPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getPreviewContent()
	 */
	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPartitioningConfiguration()
	 */
	public Object getPartitioningConfiguration()
	{
		return JSONSourceConfiguration.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getEclipsePreferences()
	 */
	@Override
	protected IEclipsePreferences getEclipsePreferences()
	{
		return InstanceScope.INSTANCE.getNode(JSONPlugin.PLUGIN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getFormatterTabPolicy()
	 */
	@Override
	protected String getFormatterTabPolicy(Map<String, String> preferences)
	{
		return preferences.get(IPreferenceConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getEditorTabSize()
	 */
	@Override
	protected int getEditorTabSize()
	{
		return EditorUtil.getSpaceIndentSize(JSONPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getDefaultEditorTabSize()
	 */
	protected int getDefaultEditorTabSize()
	{
		return EditorUtil.getDefaultSpaceIndentSize(JSONPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getFormatterTabSizeKey()
	 */
	@Override
	protected String getFormatterTabSizeKey()
	{
		return IPreferenceConstants.FORMATTER_TAB_SIZE;
	}
}
