package com.aptana.theme.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.EclipseUtil;
import com.aptana.theme.ThemePlugin;
import com.aptana.theme.internal.ThemeManager;

public class ThemerPreferenceInitializer extends AbstractPreferenceInitializer
{

	public ThemerPreferenceInitializer()
	{
		super();
	}

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = new DefaultScope().getNode(ThemePlugin.PLUGIN_ID);
		if (EclipseUtil.isStandalone())
		{
			// When they're using our standalone, use Twilight copy.
			node.put(ThemeManager.ACTIVE_THEME, "Aptana Studio"); //$NON-NLS-1$
		}
		else
		{
			// When we're installed as a plugin use our old coloring. "Eclipse" theme is too boring.
			node.put(ThemeManager.ACTIVE_THEME, "Aptana Studio 2.x"); //$NON-NLS-1$
		}
		try
		{
			node.flush();
		}
		catch (BackingStoreException e)
		{
			// ignore
		}
	}

}
