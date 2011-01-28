/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
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
	public static final boolean DEFAULT_AUTO_INDENT_ON_RETURN = true;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = (new DefaultScope()).getNode(JSPlugin.PLUGIN_ID);
		
		prefs.putBoolean(IPreferenceConstants.COMMENT_INDENT_USE_STAR, DEFAULT_COMMENT_INDENT_USE_STAR);
		prefs.putBoolean(IPreferenceConstants.AUTO_INDENT_ON_CARRIAGE_RETURN, DEFAULT_AUTO_INDENT_ON_RETURN);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);
		prefs.putDouble(IPreferenceConstants.JS_INDEX_VERSION, 0);
		prefs.put(IPreferenceConstants.JS_ACTIVATION_CHARACTERS, "."); //$NON-NLS-1$

	}
}
