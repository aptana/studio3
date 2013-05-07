/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;

/**
 * @author Max Stepanov
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	private static final String MIGRATED_AUTO_REFRESH = "migrated_auto_refresh"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = EclipseUtil.defaultScope().getNode(CorePlugin.PLUGIN_ID);
		prefs.putBoolean(ICorePreferenceConstants.PREF_SHOW_SYSTEM_JOBS, ICorePreferenceConstants.DEFAULT_DEBUG_MODE);
		prefs.put(ICorePreferenceConstants.PREF_DEBUG_LEVEL, IdeLog.StatusLevel.ERROR.toString());
		prefs.put(
				ICorePreferenceConstants.PREF_WEB_FILES,
				"*.js;*.htm;*.html;*.xhtm;*.xhtml;*.css;*.xml;*.xsl;*.xslt;*.fla;*.gif;*.jpg;*.jpeg;*.php;*.asp;*.jsp;*.png;*.as;*.sdoc;*.swf;*.shtml;*.txt;*.aspx;*.asmx;"); //$NON-NLS-1$
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}

		// Migrate auto-refresh pref to Eclipse's
		prefs = EclipseUtil.instanceScope().getNode(CorePlugin.PLUGIN_ID);
		boolean migrated = prefs.getBoolean(MIGRATED_AUTO_REFRESH, false);
		if (!migrated)
		{
			// by default, turn on auto refresh
			ResourcesPlugin
					.getPlugin()
					.getPluginPreferences()
					.setValue(ResourcesPlugin.PREF_AUTO_REFRESH, ICorePreferenceConstants.DEFAULT_AUTO_REFRESH_PROJECTS);

			// Listen for user changing auto-refresh value, when they do, don't automatically turn it on for them
			// anymore
			ResourcesPlugin.getPlugin().getPluginPreferences()
					.addPropertyChangeListener(new Preferences.IPropertyChangeListener()
					{

						public void propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent event)
						{
							if (ResourcesPlugin.PREF_AUTO_REFRESH.equals(event.getProperty()))
							{
								IEclipsePreferences ourPrefs = EclipseUtil.instanceScope()
										.getNode(CorePlugin.PLUGIN_ID);
								ourPrefs.putBoolean(MIGRATED_AUTO_REFRESH, true);
								try
								{
									ourPrefs.flush();
								}
								catch (BackingStoreException e)
								{
									IdeLog.logError(CorePlugin.getDefault(),
											"Failed to store boolean to avoid overriding auto-refresh setting", e); //$NON-NLS-1$
								}
								ResourcesPlugin.getPlugin().getPluginPreferences().removePropertyChangeListener(this);
							}
						}
					});
		}
	}
}
