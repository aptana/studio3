/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonSourceViewerConfiguration;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = EclipseUtil.defaultScope().getNode(CommonEditorPlugin.PLUGIN_ID);

		prefs.putBoolean(IPreferenceConstants.ENABLE_CHARACTER_PAIR_COLORING, true);
		prefs.put(IPreferenceConstants.CHARACTER_PAIR_COLOR, "128,128,128"); //$NON-NLS-1$
		prefs.putBoolean(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, false);

		// Tasks
		prefs.put(ICorePreferenceConstants.TASK_TAG_NAMES, "TODO,FIXME,XXX"); //$NON-NLS-1$
		prefs.put(ICorePreferenceConstants.TASK_TAG_PRIORITIES, "NORMAL,HIGH,NORMAL"); //$NON-NLS-1$
		prefs.putBoolean(ICorePreferenceConstants.TASK_TAGS_CASE_SENSITIVE, true);

		// mark occurrences
		prefs.putBoolean(IPreferenceConstants.EDITOR_MARK_OCCURRENCES, false);
		// camelCase selection
		prefs.putBoolean(IPreferenceConstants.EDITOR_SUB_WORD_NAVIGATION, true);

		// content assist
		prefs.putInt(IPreferenceConstants.CONTENT_ASSIST_DELAY,
				CommonSourceViewerConfiguration.DEFAULT_CONTENT_ASSIST_DELAY);
		prefs.putBoolean(IPreferenceConstants.CONTENT_ASSIST_AUTO_INSERT, true);
		prefs.putBoolean(IPreferenceConstants.CONTENT_ASSIST_HOVER, true);

		// insert matching characters
		prefs.putBoolean(IPreferenceConstants.EDITOR_PEER_CHARACTER_CLOSE, true);

		// wrap selection
		prefs.putBoolean(IPreferenceConstants.EDITOR_WRAP_SELECTION, true);

		// save-action for removing the trailing whitespace
		prefs.putBoolean(IPreferenceConstants.EDITOR_REMOVE_TRAILING_WHITESPACE, false);

		// enable folding
		prefs.putBoolean(IPreferenceConstants.EDITOR_ENABLE_FOLDING, true);

		// default scopes for spell checking
		prefs.put(IPreferenceConstants.ENABLED_SPELLING_SCOPES,
				"comment.block.documentation,comment.line,comment.block"); //$NON-NLS-1$

		// Set the default max cap on # of columns to color per line (a perf fix).
		int maxCols = IPreferenceConstants.EDITOR_MAX_COLORED_COLUMNS_DEFAULT;
		try
		{
			// Load up the command line value for max columns colored
			String maxColsVal = System.getProperty(IPreferenceConstants.EDITOR_MAX_COLORED_COLUMNS);
			if (!StringUtil.isEmpty(maxColsVal))
			{
				maxCols = Integer.parseInt(maxColsVal);
			}
		}
		catch (NumberFormatException e)
		{
			// ignore
		}
		prefs.putInt(IPreferenceConstants.EDITOR_MAX_COLORED_COLUMNS, maxCols);
	}
}
