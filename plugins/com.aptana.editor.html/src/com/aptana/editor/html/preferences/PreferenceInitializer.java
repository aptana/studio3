/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.editor.html.HTMLPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = (new DefaultScope()).getNode(HTMLPlugin.PLUGIN_ID);

		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);
		prefs.putDouble(IPreferenceContants.HTML_INDEX_VERSION, 0);
		prefs.put(IPreferenceContants.HTML_ACTIVATION_CHARACTERS, "</>=&'\""); //$NON-NLS-1$
		prefs.putBoolean(IPreferenceContants.HTML_AUTO_CLOSE_TAG_PAIRS, true);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_AUTO_INDENT, true);
	}
}
