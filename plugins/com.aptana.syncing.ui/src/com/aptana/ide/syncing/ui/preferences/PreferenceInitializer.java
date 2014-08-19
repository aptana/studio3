/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.ide.syncing.ui.SyncingUIPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(SyncingUIPlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.VIEW_MODE, IPreferenceConstants.VIEW_FLAT);
		prefs.put(IPreferenceConstants.DIRECTION_MODE, IPreferenceConstants.DIRECTION_BOTH);
	}
}
