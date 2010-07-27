package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.configurations.processor.IConfigurationProcessor;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * Ruby installation action controller (for Windows OS).
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class RubyInstallActionController extends AbstractActionController
{

	/**
	 * Install Ruby and DevKit on Windows platform.<br>
	 * The action runs in a different thread, and the immediate response of calling it is a JSON 'ok'. This action is
	 * ignored and return JSON indication of an error in case the OS is not Windows.
	 * 
	 * @param attributes
	 *            Should contains an array of URLs. The first URL should point to the RubyInstaller.exe, the second
	 *            should point to the DevKit.7z.
	 * @return An immediate JSON status of 'ok', unless the OS is not Windows (where 'error' is returned).
	 */
	@ControllerAction
	public Object install(final Object attributes)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			final IConfigurationProcessor processor = getProcessor();
			// Start a Job that will call the processor to compute the installed Gems and notify the browse when it's
			// done
			Job computationJob = new Job(Messages.RubyInstallActionController_installingRuby)
			{
				protected IStatus run(IProgressMonitor monitor)
				{
					processor.addConfigurationProcessorListener(RubyInstallActionController.this);
					processor.configure(monitor, attributes);
					processor.removeConfigurationProcessorListener(RubyInstallActionController.this);
					monitor.done();
					return Status.OK_STATUS;
				}
			};
			computationJob.schedule();
			return IBrowserNotificationConstants.JSON_OK;
		}
		else
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	@Override
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// TODO - Shalom: Notify that the installation is complete?
		System.out.println("configurationStateChanged: " + status.getStatus());
	}
}
