package com.aptana.git.internal.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = new DefaultScope().getNode(GitPlugin.getPluginId());
		// turn on git pull indicator calculation on all non-win OSes
		prefs.putBoolean(IPreferenceConstants.GIT_CALCULATE_PULL_INDICATOR, !Platform.getOS().equals(Platform.OS_WIN32));

		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			GitPlugin.logError(e.getMessage(), e);
		}
	}

}
