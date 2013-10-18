/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.progress.UIJob;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.util.UIUtils;
import com.aptana.usage.FeatureEvent;
import com.aptana.usage.StudioAnalytics;

/**
 * @author Nam Le <nle@appcelerator.com>
 */
public class PerspectiveChangeResetListener extends PerspectiveAdapter
{
	private static final String PERSPECTIVE_ACTIVATE_EVENT = "perspective.activate.{0}"; //$NON-NLS-1$

	private String pluginId, preferenceId, perspectiveId;
	private int perspectiveVersion;

	public PerspectiveChangeResetListener(String perspectiveId, String pluginId, String preferenceId,
			int perspectiveVersion)
	{
		this.perspectiveId = perspectiveId;
		this.pluginId = pluginId;
		this.preferenceId = preferenceId;
		this.perspectiveVersion = perspectiveVersion;
	}

	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
	{
		if (perspectiveId != null && perspective != null && perspectiveId.equals(perspective.getId()))
		{
			int version = Platform.getPreferencesService().getInt(pluginId, preferenceId, 0, null);
			if (perspectiveVersion > version)
			{
				resetPerspective(page);
				// we will only ask once regardless if user chose to update the perspective
				IEclipsePreferences prefs = (EclipseUtil.instanceScope()).getNode(pluginId);
				prefs.putInt(preferenceId, perspectiveVersion);
				try
				{
					prefs.flush();
				}
				catch (BackingStoreException e)
				{
					// ignores the exception
				}
			}
		}
		String perspectiveId = perspective.getId();
		if (!StringUtil.isEmpty(perspectiveId))
		{
			int index = perspectiveId.lastIndexOf("."); //$NON-NLS-1$
			if (index > -1)
			{
				perspectiveId = perspectiveId.substring(index + 1);
			}
			StudioAnalytics.getInstance().sendEvent(
					new FeatureEvent(MessageFormat.format(PERSPECTIVE_ACTIVATE_EVENT, perspectiveId), null));
		}
	}

	private void resetPerspective(final IWorkbenchPage page)
	{
		UIJob job = new UIJob("Resetting Studio perspective...") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (MessageDialog.openQuestion(UIUtils.getActiveShell(),
						com.aptana.ui.Messages.UIPlugin_ResetPerspective_Title,
						com.aptana.ui.Messages.UIPlugin_ResetPerspective_Description))
				{
					page.resetPerspective();
				}
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}
}
