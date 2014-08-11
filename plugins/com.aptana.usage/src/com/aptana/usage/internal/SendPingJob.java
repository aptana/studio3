/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage.internal;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.CorePlugin;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.usage.AnalyticsEvent;
import com.aptana.usage.AnalyticsInfo;
import com.aptana.usage.FeatureEvent;
import com.aptana.usage.StudioAnalytics;
import com.aptana.usage.UsagePlugin;
import com.aptana.usage.preferences.IPreferenceConstants;

public class SendPingJob extends Job
{

	public SendPingJob()
	{
		super("Sending ping"); //$NON-NLS-1$
		setSystem(true);
	}

	private static final String STUDIO_FIRST_RUN = "studio.first-run"; //$NON-NLS-1$
	private static final String STUDIO_ENROLL = "ti.enroll"; //$NON-NLS-1$
	// Events (since this is migrated from TiStudio, we will keep the constants as ti.*)
	private static final String STUDIO_START = "ti.start"; //$NON-NLS-1$
	private static final String STUDIO_END = "ti.end"; //$NON-NLS-1$

	private static final Map<String, String> STUDIO_NATURE_MAP;
	static
	{
		// @formatter:off
		STUDIO_NATURE_MAP = CollectionsUtil.newMap(
			"com.aptana.projects.webnature", "web", //$NON-NLS-1$ //$NON-NLS-2$
			"com.aptana.editor.php.phpNature", "php", //$NON-NLS-1$ //$NON-NLS-2$
			"com.aptana.ruby.core.rubynature", "ruby", //$NON-NLS-1$ //$NON-NLS-2$
			"org.radrails.rails.core.railsnature", "rails"); //$NON-NLS-1$ //$NON-NLS-2$
		// @formatter:on
	}

	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		earlyStartup();
		return Status.OK_STATUS;
	}

	public void earlyStartup()
	{
		sendFirstRunEvent();

		enroll();

		// Hook up ping when we delete a project
		ResourcesPlugin.getWorkspace()
				.addResourceChangeListener(projectDeleteListener, IResourceChangeEvent.PRE_DELETE);

		// Send ping when we start studio
		sendStartEvent();
	}

	private void sendStartEvent()
	{
		if (!EclipseUtil.isTesting())
		{
			StudioAnalytics.getInstance().sendEvent(new AnalyticsEvent(STUDIO_START, STUDIO_START, null));
		}
	}

	private boolean enroll()
	{
		IScopeContext scope = EclipseUtil.configurationScope();
		boolean hasEnrolled = Platform.getPreferencesService().getBoolean(UsagePlugin.PLUGIN_ID,
				IPreferenceConstants.HAS_ENROLLED, false, new IScopeContext[] { scope });
		if (!hasEnrolled)
		{
			AnalyticsInfo info = UsagePlugin.getDefault().getAnalyticsInfoManager()
					.getInfo("com.aptana.usage.analytics"); //$NON-NLS-1$
			String guid = info.getAppGuid();
			// only sends the enroll ping if it's Aptana Studio
			if ((new DefaultAnalyticsInfo()).getAppGuid().equals(guid))
			{
				// @formatter:off
				Map<String, String> payload = CollectionsUtil.newInOrderMap(
					"guid", guid, //$NON-NLS-1$
					"mid", CorePlugin.getMID()); //$NON-NLS-1$
				// @formatter:on

				StudioAnalytics.getInstance().sendEvent(new AnalyticsEvent(STUDIO_ENROLL, STUDIO_ENROLL, payload));
			}

			IEclipsePreferences store = scope.getNode(UsagePlugin.PLUGIN_ID);
			store.putBoolean(IPreferenceConstants.HAS_ENROLLED, true);
			try
			{
				store.flush();
				return true;
			}
			catch (BackingStoreException e)
			{
				UsagePlugin.logError(e);
			}
		}
		return false;
	}

	// Send a first-run ping if it is the first time this instance of Studio is launched
	private boolean sendFirstRunEvent()
	{
		IScopeContext scope = EclipseUtil.configurationScope();
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
					return true;
				}
				catch (BackingStoreException e)
				{
					UsagePlugin.logError(e);
				}
			}
		}
		return false;
	}

	private static IResourceChangeListener projectDeleteListener = new IResourceChangeListener()
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
					if (!ArrayUtil.isEmpty(natures))
					{
						// just checking the primary nature
						String projectType = STUDIO_NATURE_MAP.get(natures[0]);
						if (!StringUtil.isEmpty(projectType))
						{
							sendProjectDeleteEvent(project, projectType);
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

	private static void sendProjectDeleteEvent(IProject project, String projectType)
	{
		Map<String, String> payload = CollectionsUtil.newMap("name", project.getName()); //$NON-NLS-1$
		StudioAnalytics.getInstance().sendEvent(new FeatureEvent("project.delete." + projectType, payload)); //$NON-NLS-1$
	}

	public void shutdown()
	{
		cancel();

		if (projectDeleteListener != null)
		{
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(projectDeleteListener);
		}

		// Send ping when we exit studio
		if (!EclipseUtil.isTesting())
		{
			StudioAnalytics.getInstance().sendEvent(new AnalyticsEvent(STUDIO_END, STUDIO_END, null));
		}
	}
}
