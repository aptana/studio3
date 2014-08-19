/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.editor.js.JSPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	public static final boolean DEFAULT_COMMENT_INDENT_USE_STAR = true;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(JSPlugin.PLUGIN_ID);

		prefs.putBoolean(IPreferenceConstants.COMMENT_INDENT_USE_STAR, DEFAULT_COMMENT_INDENT_USE_STAR);
		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);
		prefs.put(
				com.aptana.editor.common.contentassist.IPreferenceConstants.COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS,
				"."); //$NON-NLS-1$
		prefs.put(
				com.aptana.editor.common.contentassist.IPreferenceConstants.CONTEXT_INFORMATION_ACTIVATION_CHARACTERS,
				".("); //$NON-NLS-1$
		// https://jira.appcelerator.org/browse/APSTUD-4665
		//		prefs.put(com.aptana.editor.common.contentassist.IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS, "."); //$NON-NLS-1$
		prefs.put(com.aptana.editor.common.contentassist.IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS, ""); //$NON-NLS-1$
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_AUTO_INDENT, true);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_ENABLE_FOLDING, true);

		// mark occurrences
		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_MARK_OCCURRENCES, true);
	}
}
