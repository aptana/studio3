/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import java.util.UUID;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.IStartup;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.EclipseUtil;
import com.aptana.usage.preferences.IPreferenceConstants;

public class PingStartup implements IStartup
{

	private static final String STUDIO_FIRST_RUN = "studio.first-run"; //$NON-NLS-1$

	public void earlyStartup()
	{
		// Send a first-run ping if it is the first time this instance of Studio is launched
		ConfigurationScope scope = EclipseUtil.configurationScope();
		boolean hasRun = Platform.getPreferencesService().getBoolean(UsagePlugin.PLUGIN_ID,
				IPreferenceConstants.P_IDE_HAS_RUN, false, new IScopeContext[] { scope });
		if (!hasRun)
		{
			// checks with the previous plugin id
			hasRun = Platform.getPreferencesService().getBoolean(UsagePlugin.OLD_PLUGIN_ID,
					IPreferenceConstants.P_IDE_HAS_RUN, false, new IScopeContext[] { scope });
			if (!hasRun)
			{
				StudioAnalytics.getInstance().sendEvent(new AnalyticsEvent(STUDIO_FIRST_RUN, STUDIO_FIRST_RUN, null));

				IEclipsePreferences store = scope.getNode(UsagePlugin.PLUGIN_ID);
				store.putBoolean(IPreferenceConstants.P_IDE_HAS_RUN, true);
				try
				{
					store.flush();
				}
				catch (BackingStoreException e)
				{
					UsagePlugin.logError(e);
				}
			}
		}
	}

	public static String getApplicationId()
	{
		String id = Platform.getPreferencesService().getString(UsagePlugin.PLUGIN_ID, IPreferenceConstants.P_IDE_ID,
				null, null);
		boolean save = false;
		if (id == null)
		{
			// see if there is an old id we could migrate
			id = Platform.getPreferencesService().getString(UsagePlugin.OLD_PLUGIN_ID, IPreferenceConstants.P_IDE_ID,
					null, null);
			if (id != null)
			{
				save = true;
			}
		}
		if (id == null)
		{
			id = UUID.randomUUID().toString();
			save = true;
		}
		if (save)
		{
			// saves the id in configuration scope so it's shared by all workspaces
			IEclipsePreferences prefs = EclipseUtil.configurationScope().getNode(UsagePlugin.PLUGIN_ID);
			prefs.put(IPreferenceConstants.P_IDE_ID, id);
			try
			{
				prefs.flush();
			}
			catch (BackingStoreException e)
			{
				UsagePlugin.logError(e);
			}
		}
		return id;
	}
}
