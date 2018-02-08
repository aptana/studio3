/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.css.CSSPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(CSSPlugin.PLUGIN_ID);

		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_AUTO_INDENT, true);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_ENABLE_FOLDING, true);
		prefs.put(
				com.aptana.editor.common.contentassist.IPreferenceConstants.COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS,
				".#:"); //$NON-NLS-1$
		prefs.put(
				com.aptana.editor.common.contentassist.IPreferenceConstants.CONTEXT_INFORMATION_ACTIVATION_CHARACTERS,
				"(,"); //$NON-NLS-1$
		prefs.put(com.aptana.editor.common.contentassist.IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS,
				StringUtil.EMPTY);

		// mark occurrences
		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_MARK_OCCURRENCES, true);
	}
}
