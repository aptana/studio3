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
 *******************************************************************************/
package com.aptana.ruby.formatter;

import java.net.URL;
import java.util.Map;

import com.aptana.formatter.AbstractScriptFormatterFactory;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;
import com.aptana.formatter.ui.IScriptFormatter;
import com.aptana.ruby.formatter.preferences.RubyFormatterModifyDialog;
import com.aptana.ui.preferences.PreferenceKey;

public class RubyFormatterFactory extends AbstractScriptFormatterFactory {

	private static final String FORMATTER_PREVIEW_FILE = "formatterPreview.rb"; //$NON-NLS-1$
	
	private static final String[] KEYS = {
			RubyFormatterConstants.FORMATTER_TAB_CHAR,
			RubyFormatterConstants.FORMATTER_INDENTATION_SIZE,
			RubyFormatterConstants.FORMATTER_TAB_SIZE,
			RubyFormatterConstants.INDENT_CLASS,
			RubyFormatterConstants.INDENT_MODULE,
			RubyFormatterConstants.INDENT_METHOD,
			RubyFormatterConstants.INDENT_BLOCKS,
			RubyFormatterConstants.INDENT_IF,
			RubyFormatterConstants.INDENT_CASE,
			RubyFormatterConstants.INDENT_WHEN,
			RubyFormatterConstants.LINES_FILE_AFTER_REQUIRE,
			RubyFormatterConstants.LINES_FILE_BETWEEN_MODULE,
			RubyFormatterConstants.LINES_FILE_BETWEEN_CLASS,
			RubyFormatterConstants.LINES_FILE_BETWEEN_METHOD,
			RubyFormatterConstants.LINES_BEFORE_FIRST,
			RubyFormatterConstants.LINES_BEFORE_MODULE,
			RubyFormatterConstants.LINES_BEFORE_CLASS,
			RubyFormatterConstants.LINES_BEFORE_METHOD,
			RubyFormatterConstants.LINES_PRESERVE,
			RubyFormatterConstants.WRAP_COMMENTS,
			RubyFormatterConstants.WRAP_COMMENTS_LENGTH };

	public PreferenceKey[] getPreferenceKeys()
	{
		final PreferenceKey[] result = new PreferenceKey[KEYS.length];
		for (int i = 0; i < KEYS.length; ++i)
		{
			final String key = KEYS[i];
			final String qualifier;
			if (RubyFormatterConstants.FORMATTER_TAB_CHAR.equals(key)
					|| RubyFormatterConstants.FORMATTER_INDENTATION_SIZE.equals(key)
					|| RubyFormatterConstants.FORMATTER_TAB_SIZE.equals(key))
			{
				qualifier = FormatterPlugin.PLUGIN_ID;
			}
			else
			{
				qualifier = RubyFormatterPlugin.PLUGIN_ID;
			}
			result[i] = new PreferenceKey(qualifier, key);
		}
		return result;
	}

	public IScriptFormatter createFormatter(String lineDelimiter, Map<String, String> preferences)
	{
		return new RubyFormatter(lineDelimiter, preferences);
	}

	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new RubyFormatterModifyDialog(dialogOwner, this);
	}

	@Override
	public PreferenceKey getActiveProfileKey()
	{
		// FIXME - Shalom: Implements this
		return null;
	}

}
