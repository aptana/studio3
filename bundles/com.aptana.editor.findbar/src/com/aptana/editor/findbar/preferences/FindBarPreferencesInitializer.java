/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.editor.findbar.FindBarPlugin;

/**
 * @author Fabio Zadrozny
 */
public class FindBarPreferencesInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences pref = DefaultScope.INSTANCE.getNode(FindBarPlugin.PLUGIN_ID);

		pref.putBoolean(IPreferencesConstants.USE_CUSTOM_FIND_BAR, true);
		pref.putBoolean(IPreferencesConstants.INCREMENTAL_SEARCH_ON_FIND_BAR, false); // default not incremental!
		pref.putBoolean(IPreferencesConstants.CTRL_F_TWICE_OPENS_ECLIPSE_FIND_BAR, false);
	}

}
