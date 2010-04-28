package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;
import org.mortbay.util.ajax.JSON;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.configurations.processor.IConfigurationProcessor;
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
	 * Opens the Eclipse P2 Plug-ins dialog.
	 */
	@ControllerAction
	public Object openPluginsDialog()
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
				processor.configure(monitor, null);
				processor.removeConfigurationProcessorListener(PluginsActionController.this);
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

	/*
	 * (non-Javadoc)
	 * @seecom.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	@SuppressWarnings("unchecked")
	@Override
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
