/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Set;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * An action controller for calling Studio's command handlers that were contributed through the
 * 'org.eclipse.ui.commands' extension point.<br>
 * Executing example (for JS with Prototype):
 * <code>result = dispatch($H({controller:'portal.commands', action:"execute", args : [commandId].toJSON()}).toJSON());</code>
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class CommandHandlerActionController extends AbstractActionController
{
	// ############## Actions ###############

	/**
	 * Executes a command handler.<br>
	 * 
	 * @param attributes
	 *            We expect for an array that contains a single string Id for the command.
	 */
	@ControllerAction
	public Object execute(Object attributes)
	{
		String commandId = getCommandId(attributes);
		if (commandId == null)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		try
		{
			IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
					IHandlerService.class);
			handlerService.executeCommand(commandId, null);
		}
		catch (Exception e)
		{
			PortalUIPlugin.logError(e);
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		return IBrowserNotificationConstants.JSON_OK;
	}

	/**
	 * Returns the command-Id from the attributes. Null, if an error occurred.
	 * 
	 * @param attributes
	 * @return A command Id, or null if an error occurs.
	 */
	private String getCommandId(Object attributes)
	{
		if (attributes instanceof Object[])
		{
			Object[] arr = (Object[]) attributes;
			if (arr.length == 1 && arr[0] != null)
			{
				return arr[0].toString();
			}
			else
			{
				PortalUIPlugin
						.logError(new Exception(
								"Wrong argument count passed to CommandHandlerActionController::execute. Expected 1 and got " + arr.length));//$NON-NLS-1$
			}
		}
		else
		{
			PortalUIPlugin.logError(new Exception(
					"Wrong argument type passed to CommandHandlerActionController::execute. Expected Object[] and got " //$NON-NLS-1$
							+ ((attributes == null) ? "null" : attributes.getClass().getName()))); //$NON-NLS-1$s
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// Nothing to do here...
	}

}
