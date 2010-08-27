package com.aptana.theme.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.theme.ThemePlugin;
import com.aptana.theme.preferences.IPreferenceConstants;

public class ThemerPreferenceInitializer extends AbstractPreferenceInitializer
{

	public static final String DEFAULT_THEME = "Aptana Studio"; //$NON-NLS-1$

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = new DefaultScope().getNode(ThemePlugin.PLUGIN_ID);
		node.put(IPreferenceConstants.ACTIVE_THEME, DEFAULT_THEME);
	}
}
