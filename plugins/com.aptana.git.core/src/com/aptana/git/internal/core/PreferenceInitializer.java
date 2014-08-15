/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(GitPlugin.getPluginId());
		// turn on git pull indicator calculation on all non-win OSes
		prefs.putBoolean(IPreferenceConstants.GIT_CALCULATE_PULL_INDICATOR, false);
		// By default, auto-attach projects to our git support if they have a repo
		prefs.putBoolean(IPreferenceConstants.AUTO_ATTACH_REPOS, true);
		prefs.putBoolean(IPreferenceConstants.IGNORE_NO_GIT, false);
	}

}
