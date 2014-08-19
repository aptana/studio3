/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter;

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
import com.aptana.editor.xml.XMLPlugin;
import com.aptana.editor.xml.XMLSourceConfiguration;
import com.aptana.editor.xml.XMLSourceViewerConfiguration;
import com.aptana.editor.xml.formatter.preferences.XMLFormatterModifyDialog;
import com.aptana.formatter.AbstractScriptFormatterFactory;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;

public class XMLFormatterFactory extends AbstractScriptFormatterFactory
{

	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(XMLFormatterPlugin.PLUGIN_ID,
			XMLFormatterConstants.FORMATTER_ID);

	private static final String FORMATTER_PREVIEW_FILE = "formatterPreview.xml"; //$NON-NLS-1$

	private static final String[] KEYS = { XMLFormatterConstants.FORMATTER_INDENTATION_SIZE,
			XMLFormatterConstants.FORMATTER_TAB_CHAR, XMLFormatterConstants.FORMATTER_TAB_SIZE,
			XMLFormatterConstants.WRAP_COMMENTS, XMLFormatterConstants.WRAP_COMMENTS_LENGTH,
			XMLFormatterConstants.INDENT_EXCLUDED_TAGS, XMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS,
			XMLFormatterConstants.NEW_LINES_EXCLUDED_ON_TEXT_NODES, XMLFormatterConstants.LINES_AFTER_ELEMENTS,
			XMLFormatterConstants.LINES_AFTER_NON_XML_ELEMENTS, XMLFormatterConstants.LINES_BEFORE_NON_XML_ELEMENTS,
			XMLFormatterConstants.PRESERVED_LINES, XMLFormatterConstants.FORMATTER_OFF_ON_ENABLED,
			XMLFormatterConstants.FORMATTER_ON, XMLFormatterConstants.FORMATTER_OFF };

	public PreferenceKey[] getPreferenceKeys()
	{
		final PreferenceKey[] result = new PreferenceKey[KEYS.length];
		for (int i = 0; i < KEYS.length; ++i)
		{
			final String key = KEYS[i];
			result[i] = new PreferenceKey(XMLFormatterPlugin.PLUGIN_ID, key);
		}
		return result;
	}

	public IScriptFormatter createFormatter(String lineSeparator, Map<String, String> preferences)
	{
		return new XMLFormatter(lineSeparator, preferences, getMainContentType());
	}

	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new XMLFormatterModifyDialog(dialogOwner, this);
	}

	public SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		return new XMLSourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	public PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER_PREF_KEY;
	}

	public IPreferenceStore getPreferenceStore()
	{
		return XMLFormatterPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPartitioningConfiguration()
	 */
	public Object getPartitioningConfiguration()
	{
		return XMLSourceConfiguration.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getEclipsePreferences()
	 */
	@Override
	protected IEclipsePreferences getEclipsePreferences()
	{
		return InstanceScope.INSTANCE.getNode(XMLPlugin.PLUGIN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getFormatterTabPolicy()
	 */
	@Override
	protected String getFormatterTabPolicy(Map<String, String> preferences)
	{
		return preferences.get(XMLFormatterConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getEditorTabSize()
	 */
	@Override
	protected int getEditorTabSize()
	{
		return EditorUtil.getSpaceIndentSize(XMLPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getDefaultEditorTabSize()
	 */
	protected int getDefaultEditorTabSize()
	{
		return EditorUtil.getDefaultSpaceIndentSize(XMLPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatterFactory#getFormatterTabSizeKey()
	 */
	@Override
	protected String getFormatterTabSizeKey()
	{
		return XMLFormatterConstants.FORMATTER_TAB_SIZE;
	}
}
