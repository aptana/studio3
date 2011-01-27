/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonSourceViewerConfiguration;

@SuppressWarnings("restriction")
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = new DefaultScope().getNode(CommonEditorPlugin.PLUGIN_ID);

		prefs.putBoolean(IPreferenceConstants.ENABLE_CHARACTER_PAIR_COLORING, true);
		prefs.put(IPreferenceConstants.CHARACTER_PAIR_COLOR, "128,128,128"); //$NON-NLS-1$
		prefs.putBoolean(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);

		// Tasks
		prefs.put(IPreferenceConstants.TASK_TAG_NAMES, "TODO,FIXME,XXX"); //$NON-NLS-1$
		prefs.put(IPreferenceConstants.TASK_TAG_PRIORITIES, "NORMAL,HIGH,NORMAL"); //$NON-NLS-1$
		prefs.putBoolean(IPreferenceConstants.TASK_TAGS_CASE_SENSITIVE, true);

		prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, true);
		prefs.putInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 2);
		prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER, true);

		// mark occurrences
		prefs.putBoolean(IPreferenceConstants.EDITOR_MARK_OCCURRENCES, true);

		// content assist
		prefs.putInt(IPreferenceConstants.CONTENT_ASSIST_DELAY,
				CommonSourceViewerConfiguration.DEFAULT_CONTENT_ASSIST_DELAY);

		// insert matching characters
		prefs.putBoolean(IPreferenceConstants.EDITOR_PEER_CHARACTER_CLOSE, true);
	}

}
