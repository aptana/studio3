/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *     Aptana Inc. - Improved support (Shalom Gibly)
 *******************************************************************************/
package com.aptana.editor.ruby.formatter;

import java.net.URL;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.ruby.RubySourceConfiguration;
import com.aptana.editor.ruby.RubySourceViewerConfiguration;
import com.aptana.editor.ruby.formatter.preferences.RubyFormatterModifyDialog;
import com.aptana.formatter.AbstractScriptFormatterFactory;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;

public class RubyFormatterFactory extends AbstractScriptFormatterFactory
{

	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(RubyFormatterPlugin.PLUGIN_ID,
			RubyFormatterConstants.FORMATTER_ID);

	private static final String FORMATTER_PREVIEW_FILE = "formatterPreview.rb"; //$NON-NLS-1$

	private static final String[] KEYS = { RubyFormatterConstants.FORMATTER_TAB_CHAR,
			RubyFormatterConstants.FORMATTER_INDENTATION_SIZE, RubyFormatterConstants.FORMATTER_TAB_SIZE,
			RubyFormatterConstants.INDENT_CLASS, RubyFormatterConstants.INDENT_MODULE,
			RubyFormatterConstants.INDENT_METHOD, RubyFormatterConstants.INDENT_BLOCKS,
			RubyFormatterConstants.INDENT_IF, RubyFormatterConstants.INDENT_CASE, RubyFormatterConstants.INDENT_WHEN,
			RubyFormatterConstants.LINES_FILE_AFTER_REQUIRE, RubyFormatterConstants.LINES_FILE_BETWEEN_MODULE,
			RubyFormatterConstants.LINES_FILE_BETWEEN_CLASS, RubyFormatterConstants.LINES_FILE_BETWEEN_METHOD,
			RubyFormatterConstants.LINES_BEFORE_FIRST, RubyFormatterConstants.LINES_BEFORE_MODULE,
			RubyFormatterConstants.LINES_BEFORE_CLASS, RubyFormatterConstants.LINES_BEFORE_METHOD,
			RubyFormatterConstants.LINES_PRESERVE, RubyFormatterConstants.WRAP_COMMENTS,
			RubyFormatterConstants.WRAP_COMMENTS_LENGTH };

	public PreferenceKey[] getPreferenceKeys()
	{
		final PreferenceKey[] result = new PreferenceKey[KEYS.length];
		for (int i = 0; i < KEYS.length; ++i)
		{
			final String key = KEYS[i];
			result[i] = new PreferenceKey(RubyFormatterPlugin.PLUGIN_ID, key);
		}
		return result;
	}

	public PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER_PREF_KEY;
	}

	public IScriptFormatter createFormatter(String lineDelimiter, Map<String, String> preferences)
	{
		return new RubyFormatter(lineDelimiter, preferences, getMainContentType());
	}

	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new RubyFormatterModifyDialog(dialogOwner, this);
	}

	public SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		// TODO: Shalom - Wrap this in 'simple' implementation?
		// return new SimpleRubySourceViewerConfiguration(colorManager,
		// preferenceStore, editor, IRubyPartitions.RUBY_PARTITIONING,
		// configureFormatter);
		return new RubySourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	public IPreferenceStore getPreferenceStore()
	{
		return RubyFormatterPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPartitioningConfiguration()
	 */
	public Object getPartitioningConfiguration()
	{
		return RubySourceConfiguration.getDefault();
	}
}
