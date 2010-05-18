package com.aptana.deploy.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.deploy.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = (new DefaultScope()).getNode(Activator.getPluginIdentifier());
		prefs.putBoolean(IPreferenceConstants.AUTO_SYNC, true);
	}
}
