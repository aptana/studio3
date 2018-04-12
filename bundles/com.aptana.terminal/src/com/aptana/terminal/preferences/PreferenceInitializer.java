/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.util.EclipseUtil;
import com.aptana.terminal.TerminalPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	/**
	 * PreferenceInitializer
	 */
	public PreferenceInitializer()
	{
	}

	/**
	 * initializeDefaultPreferences
	 */
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(TerminalPlugin.PLUGIN_ID);

		prefs.putBoolean(IPreferenceConstants.FIRST_RUN, true);
		prefs.putBoolean(IPreferenceConstants.CLOSE_VIEW_ON_EXIT, true);
	}
}
