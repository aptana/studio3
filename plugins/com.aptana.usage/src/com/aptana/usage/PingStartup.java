/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.IStartup;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.CorePlugin;
import com.aptana.core.util.EclipseUtil;
import com.aptana.usage.internal.AnalyticsInfo;
import com.aptana.usage.internal.AnalyticsInfoManager;
import com.aptana.usage.internal.DefaultAnalyticsInfo;
import com.aptana.usage.preferences.IPreferenceConstants;

public class PingStartup implements IStartup
{

	private static final String STUDIO_FIRST_RUN = "studio.first-run"; //$NON-NLS-1$
	private static final String STUDIO_ENROLL = "ti.enroll"; //$NON-NLS-1$
	private static final String STUDIO_START = "ti.start"; //$NON-NLS-1$

	private static final Map<String, String> STUDIO_NATURE_MAP;
	static
	{
		STUDIO_NATURE_MAP = new HashMap<String, String>();
		STUDIO_NATURE_MAP.put("com.aptana.projects.webnature", "web"); //$NON-NLS-1$ //$NON-NLS-2$
		STUDIO_NATURE_MAP.put("com.aptana.editor.php.phpNature", "php"); //$NON-NLS-1$ //$NON-NLS-2$
		STUDIO_NATURE_MAP.put("com.aptana.ruby.core.rubynature", "ruby"); //$NON-NLS-1$ //$NON-NLS-2$
		STUDIO_NATURE_MAP.put("org.radrails.rails.core.railsnature", "rails"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static IResourceChangeListener resourceListener = new IResourceChangeListener()
	{

		public void resourceChanged(IResourceChangeEvent event)
		{
			if (event.getType() == IResourceChangeEvent.PRE_DELETE)
			{
				// check if it is a studio project and then send the ping out
				try
				{
					IProject project = event.getResource().getProject();
					IProjectDescription description = project.getDescription();
					String[] natures = description.getNatureIds();
					String projectType;
					for (String nature : natures)
					{
						projectType = STUDIO_NATURE_MAP.get(nature);
						if (projectType != null)
						{
							sendProjectDeleteEvent(project, projectType);
							break;
						}
					}
				}
				catch (Exception e)
				{
					UsagePlugin.logError(e);
				}
			}
		}
	};

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

		boolean hasEnrolled = Platform.getPreferencesService().getBoolean(UsagePlugin.PLUGIN_ID,
				IPreferenceConstants.HAS_ENROLLED, false, new IScopeContext[] { scope });
		if (!hasEnrolled)
		{
			AnalyticsInfo info = AnalyticsInfoManager.getInstance().getInfo("com.aptana.usage.analytics"); //$NON-NLS-1$
			String guid = info.getAppGuid();
			// only sends the enroll ping if it's Aptana Studio
			if ((new DefaultAnalyticsInfo()).getAppGuid().equals(guid))
			{
				Map<String, String> payload = new LinkedHashMap<String, String>();
				payload.put("guid", guid); //$NON-NLS-1$
				payload.put("mid", CorePlugin.getMID()); //$NON-NLS-1$

				StudioAnalytics.getInstance().sendEvent(new AnalyticsEvent(STUDIO_ENROLL, STUDIO_ENROLL, payload));
			}

			IEclipsePreferences store = scope.getNode(UsagePlugin.PLUGIN_ID);
			store.putBoolean(IPreferenceConstants.HAS_ENROLLED, true);
			try
			{
				store.flush();
			}
			catch (BackingStoreException e)
			{
				UsagePlugin.logError(e);
			}
		}

		if (EclipseUtil.isTesting())
		{
			StudioAnalytics.getInstance().sendEvent(new AnalyticsEvent(STUDIO_START, STUDIO_START, null));
		}

		// Hook up ping when we delete a project
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener, IResourceChangeEvent.PRE_DELETE);
	}

	public static void removeResourceListener()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
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

	private static void sendProjectDeleteEvent(IProject project, String projectType)
	{
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("name", project.getName()); //$NON-NLS-1$

		StudioAnalytics.getInstance().sendEvent(new FeatureEvent("project.delete." + projectType, payload)); //$NON-NLS-1$
	}
}
