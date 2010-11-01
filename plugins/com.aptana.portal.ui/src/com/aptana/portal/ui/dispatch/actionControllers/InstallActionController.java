/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.progress.IProgressService;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.configurations.processor.IConfigurationProcessor;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * A generic installer action controller. All install action should go through this class, while the extension point
 * defines the processor that will be in use for a specific install action.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class InstallActionController extends AbstractActionController
{

	/**
	 * Install an application.<br>
	 * The action runs in a different thread, and the immediate response of calling it is a JSON 'ok'. This action is
	 * ignored and return JSON indication of an error in case the OS is not Windows.
	 * 
	 * @param attributes
	 *            Should contains an array of URLs. The number of URLs, and the type of files they point to, is defined
	 *            and handled by the processor.
	 * @return An immediate JSON status of 'OK'.
	 */
	@ControllerAction
	public Object install(final Object attributes)
	{

		final IConfigurationProcessor processor = getProcessor();
		// Start a Job that will call the processor to compute the installed Gems and notify the browse when it's
		// done
		Job computationJob = new Job(Messages.InstallActionController_installing)
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				processor.addConfigurationProcessorListener(InstallActionController.this);
				processor.configure(monitor, attributes);
				processor.removeConfigurationProcessorListener(InstallActionController.this);
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		IWorkbench workbench = PortalUIPlugin.getDefault().getWorkbench();
		IProgressService progressService = workbench.getProgressService();

		computationJob.setPriority(Job.INTERACTIVE);
		// Display the progress bar on screen.
		progressService.showInDialog(workbench.getActiveWorkbenchWindow().getShell(), computationJob);
		computationJob.schedule();
		return IBrowserNotificationConstants.JSON_OK;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// TODO - Shalom: Notify that the installation is complete?
		// System.out.println("configurationStateChanged: " + status.getStatus());
	}
}
