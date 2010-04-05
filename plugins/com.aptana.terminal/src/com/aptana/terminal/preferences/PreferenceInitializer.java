package com.aptana.terminal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.terminal.Activator;

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
		IEclipsePreferences prefs = new DefaultScope().getNode(Activator.PLUGIN_ID);

		prefs.putBoolean(IPreferenceConstants.FIRST_RUN, true);
		prefs.putBoolean(IPreferenceConstants.CLOSE_VIEW_ON_EXIT, true);
	}
}
