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

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.mortbay.util.ajax.JSON;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.configurations.processor.IConfigurationProcessor;
import com.aptana.portal.ui.dispatch.BrowserNotifier;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * A action controller for Ruby Gems related actions.<br>
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class GemsActionController extends AbstractActionController
{
	/**
	 * Starts a Job that computes the installed Gems in the system.<br>
	 * This Job will then notify any internal browser the result through the {@link BrowserNotifier}.
	 * 
	 * @param attributes
	 *            Optional attributes that describes the gems to compute.
	 * @return An immediate {@link IBrowserNotificationConstants#JSON_OK} to indicate that the Job was started, or an
	 *         error JSON notification if there is no configuration processor that can deal with this request.
	 */
	@ControllerAction
	public Object computeInstalledGems(final Object attributes)
	{
		// Get the configuration processor (the method invocation in the AbstractActionController already checked that
		// it's valid)
		final IConfigurationProcessor processor = getProcessor();
		// Start a Job that will call the processor to compute the installed Gems and notify the browse when it's done
		Job computationJob = new Job(Messages.GemsActionController_computingGemsJobName)
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				processor.addConfigurationProcessorListener(GemsActionController.this);
				processor.getStatus(monitor, attributes, true);
				processor.removeConfigurationProcessorListener(GemsActionController.this);
				return Status.OK_STATUS;
			}
		};
		computationJob.schedule();
		return IBrowserNotificationConstants.JSON_OK;
	}

	/**
	 * Starts a Job that computes all the installed Gems in the system.<br>
	 * This Job will then notify any internal browser the result through the {@link BrowserNotifier}.
	 * 
	 * @return An immediate response with the system's gems (Note: This one happens without a Job).
	 */
	@ControllerAction
	public Object computeAllGems()
	{
		final IConfigurationProcessor processor = getProcessor();
		ConfigurationStatus status = processor.getStatus(null, null, true);
		if (status != null)
		{
			return JSON.toString(status);
		}
		return IBrowserNotificationConstants.JSON_ERROR;
	}

	/**
	 * Returns a browser notification with the current known installed gems.
	 * 
	 * @return a browser notification
	 */
	@ControllerAction
	public Object getInstalledGems()
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
	 * Listen to configuration status changes and notify the browser when needed.
	 */
	@SuppressWarnings("unchecked")
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// On the Gems controller, we only care status changes
		if (attributesChanged != null && attributesChanged.contains(ConfigurationStatus.STATUS))
		{
			String jsonStatus = JSON.toString(status);
			BrowserNotifier.getInstance().notifyBrowserInUIThread(Collections.EMPTY_LIST,
					IBrowserNotificationConstants.EVENT_ID_GEM_LIST, IBrowserNotificationConstants.EVENT_TYPE_CHANGED,
					jsonStatus);
		}
	}
}
