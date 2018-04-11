/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.configurations.processor.IConfigurationProcessor;
import com.aptana.core.util.EclipseUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.dispatch.BrowserNotifier;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * A action controller for Eclipse Plugins related actions.<br>
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class PluginsActionController extends AbstractActionController
{

	/**
	 * Opens the Eclipse P2 Plug-ins dialog.<br>
	 * When the dialog is closed, in case we have valid list of plugins to work with, the method will call
	 * {@link #computeInstalledPlugins(Object)} and return its result.
	 * 
	 * <pre>
	 * Example:
	 * dispatch($H({
	 *     controller : 'portal.plugins',
	 *     action : "openPluginsDialog",
	 *     args : [["http://appcelerator.com/appcelerator/studio/update/rc"], {
	 *         "feature_id" : "com.aptana.feature"
	 *     }].toJSON()
	 * }).toJSON());
	 * </pre>
	 * 
	 * @param attributes
	 *            A multi-dimensional array of size 2 which contains an optional update-site URL and an optional plugins
	 *            to check (just as passed to {@link #computeStatus(IProgressMonitor, Object)})
	 */
	@ControllerAction
	public Object openPluginsDialog(final Object attributes)
	{
		// Get the configuration processor (the method invocation in the AbstractActionController already checked that
		// it's valid)
		final IConfigurationProcessor processor = getProcessor();
		// Start a Job that will call the processor to compute the installed Gems and notify the browse when it's done
		Job installationJob = new UIJob(Messages.PluginsActionController_installNewSoftware)
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				processor.addConfigurationProcessorListener(PluginsActionController.this);
				final ConfigurationStatus statusResult = processor.configure(monitor, attributes);
				processor.removeConfigurationProcessorListener(PluginsActionController.this);

				// The following is specifically for the Plugin action of opening the install dialog.
				// In case that action trigger an error status event to the browser, we don't want to cache it.
				// Caching it will cause the browser to display the same error on every refresh, so we need to set the
				// status to back to Ok.
				// This nested job will fire another notification that will prevent any recurring error notifications.
				Job sendOkJob = new UIJob("Send OK Job") //$NON-NLS-1$
				{
					@SuppressWarnings("unchecked")
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						statusResult.setStatus(ConfigurationStatus.OK);
						String jsonStatus = JSON.toString(statusResult);
						BrowserNotifier.getInstance().notifyBrowserInUIThread(Collections.EMPTY_LIST,
								IBrowserNotificationConstants.EVENT_ID_PLUGINS,
								IBrowserNotificationConstants.EVENT_TYPE_CHANGED, jsonStatus);
						return Status.OK_STATUS;
					}
				};
				EclipseUtil.setSystemForJob(sendOkJob);
				sendOkJob.schedule();
				return Status.OK_STATUS;
			}
		};
		installationJob.schedule();
		return IBrowserNotificationConstants.JSON_OK;
	}

	/**
	 * Returns an immediate JSON notification result regarding the installed plugins.<br>
	 * The status of the result can be any of the {@link ConfigurationStatus} status constants.
	 * 
	 * @return An immediate JSON notification result regarding the installed plugins
	 */
	@ControllerAction
	public Object getInstalledPlugins()
	{
		IConfigurationProcessor processor = getProcessor();
		// We rely on the fact that the ConfigurationStatus implements the JSON Convertible interface
		String processorResult = JSON.toString(processor.getStatus(null, null, false));
		// Construct a response which is similar to the notification structure being fired when
		// computeInstalledGems is invoked. This makes the JS side handling easier in both case.
		return BrowserNotifier.toJSONNotification(IBrowserNotificationConstants.EVENT,
				IBrowserNotificationConstants.EVENT_TYPE_RESPONSE, processorResult);

	}

	/**
	 * This method checks the list of installed plugins vs. the given lookup list, and eventually calls a browser
	 * notification that contains the list of plugins/features IDs that were found.<br>
	 * The resulted list will hold the plugin/feature ID and its version.<br>
	 * This method will run a separate Job to do the computation asynchronously.
	 * 
	 * @param lookup
	 * @return An immediate {@link IBrowserNotificationConstants#JSON_OK} response to indicate a successful initiation
	 *         of the check.
	 */
	@ControllerAction
	public Object computeInstalledPlugins(final Object lookup)
	{
		final IConfigurationProcessor processor = getProcessor();
		// Start a Job that will call the processor to compute the installed Gems and notify the browse when it's done
		Job computationJob = new Job(Messages.PluginsActionController_computingInstalledPlugins)
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				processor.addConfigurationProcessorListener(PluginsActionController.this);
				processor.getStatus(monitor, lookup, true);
				processor.removeConfigurationProcessorListener(PluginsActionController.this);
				return Status.OK_STATUS;
			}
		};
		computationJob.schedule();
		return IBrowserNotificationConstants.JSON_OK;
	}

	/**
	 * Make a synchronous call to compute the status of the given lookup list. This call will return a JSON status
	 * string that contains the list of plugins/features IDs that were found. It's up to the caller to determine it the
	 * list contains the requested item (plugin). If it's not in the list, it's not installed.
	 * 
	 * @param lookup
	 *            Expected an multi-dimensional Object array with rows that contain: plugin-id, min-version,
	 *            update-site, feature-id.
	 * @return A JSON status for the given plugin(s)
	 */
	@ControllerAction
	public Object synchronousComputeInstalledPlugins(final Object lookup)
	{
		final IConfigurationProcessor processor = getProcessor();
		ConfigurationStatus status = processor.getStatus(new NullProgressMonitor(), lookup, true);
		String jsonStatus = JSON.toString(status);
		return jsonStatus;
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	@SuppressWarnings("unchecked")
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// We only care status changes
		if (attributesChanged != null && attributesChanged.contains(ConfigurationStatus.STATUS))
		{
			String jsonStatus = JSON.toString(status);
			BrowserNotifier.getInstance().notifyBrowserInUIThread(Collections.EMPTY_LIST,
					IBrowserNotificationConstants.EVENT_ID_PLUGINS, IBrowserNotificationConstants.EVENT_TYPE_CHANGED,
					jsonStatus);
		}

	}
}
