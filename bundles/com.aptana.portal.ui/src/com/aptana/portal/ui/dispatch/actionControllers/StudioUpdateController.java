/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.configurations.processor.IConfigurationProcessor;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.BrowserNotifier;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * An action controller that can check for any available Studio updates, and can allow initiating an installation when
 * an update is available.<br>
 * The controller uses registered {@link IConfigurationProcessor}s to do all the 'real' work, so multiple processors may
 * handle the update differently by filtering out only the plugins they are in-charge of, for example. <br>
 * <br>
 * Notes:
 * <ul>
 * <li>Both the check for update, and the installation of update, are done in a separate Job. Notifications are sent to
 * the browser when the Jobs are completed.</li>
 * <li>This controller should be registered via the <code>browserInteractions::actionController</code> extension in the
 * user-plugin. The registration should include a valid processor.</li>
 * </ul>
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class StudioUpdateController extends AbstractActionController
{
	// ////////////////////////// Actions /////////////////////////////

	/**
	 * Request a check for Studio update. A notification will be fired as an event when the check is done.
	 */
	@ControllerAction
	public Object checkForUpdate()
	{
		final IConfigurationProcessor processor = getProcessor(true);
		if (processor != null)
		{
			Job computationJob = new Job("Checking for a Studio update...") //$NON-NLS-1$ (a system Job, no need to translate)
			{
				protected IStatus run(IProgressMonitor monitor)
				{
					try
					{
						processor.addConfigurationProcessorListener(StudioUpdateController.this);
						ConfigurationStatus status = processor.getStatus(monitor, null, true);
						if (status.getStatus() != ConfigurationStatus.OK)
						{
							IdeLog.logError(PortalUIPlugin.getDefault(), "Error while checking for Studio updates"); //$NON-NLS-1$
						}
					}
					finally
					{
						processor.removeConfigurationProcessorListener(StudioUpdateController.this);
					}
					return Status.OK_STATUS;
				}
			};
			EclipseUtil.setSystemForJob(computationJob);
			computationJob.schedule();
			return IBrowserNotificationConstants.JSON_OK;
		}
		else
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), "Missing processor for the StudioUpdateController"); //$NON-NLS-1$
		}
		return IBrowserNotificationConstants.JSON_OK;
	}

	/**
	 * Request a Studio update installation. A notification will be fired as an event when the check is done.
	 */
	@ControllerAction
	public Object installUpdate()
	{
		final IConfigurationProcessor processor = getProcessor(true);
		if (processor != null)
		{
			Job computationJob = new Job("Installing a Studio update...") //$NON-NLS-1$ (a system Job, no need to translate)
			{
				protected IStatus run(IProgressMonitor monitor)
				{
					try
					{
						processor.addConfigurationProcessorListener(StudioUpdateController.this);
						ConfigurationStatus status = processor.configure(monitor, null);
						if (status.getStatus() != ConfigurationStatus.OK)
						{
							IdeLog.logError(PortalUIPlugin.getDefault(), "Error while installing a Studio update"); //$NON-NLS-1$
						}
					}
					finally
					{
						processor.removeConfigurationProcessorListener(StudioUpdateController.this);
					}
					return Status.OK_STATUS;
				}
			};
			EclipseUtil.setSystemForJob(computationJob);
			computationJob.schedule();
			return IBrowserNotificationConstants.JSON_OK;
		}
		else
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), "Missing processor for the StudioUpdateController"); //$NON-NLS-1$
			return IBrowserNotificationConstants.JSON_ERROR;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// We only care status changes
		if (attributesChanged != null && attributesChanged.contains(ConfigurationStatus.STATUS))
		{
			List<String> emptyList = Collections.emptyList();
			BrowserNotifier.getInstance().notifyBrowserInUIThread(emptyList,
					IBrowserNotificationConstants.EVENT_ID_STUDIO_UPDATE,
					IBrowserNotificationConstants.EVENT_TYPE_RESPONSE, JSON.toString(status));
		}
	}
}
