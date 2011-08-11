/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * An action controller for calling Studio's command handlers that were contributed through the
 * 'org.eclipse.ui.commands' extension point.<br>
 * There are two forms of this command - One that supports arguments and one that doesn't.<br>
 * <br>
 * Executing example (for JS with Prototype):<br>
 * <b>Command without parameters:</b><br>
 * <code>result = dispatch($H({controller:'portal.commands', action:"execute", args : [commandId].toJSON()}).toJSON());</code>
 * <br>
 * <b>Command with parameters (as map of name-value):</b><br>
 * <code>result = dispatch($H({controller:'portal.commands', action:"execute", args : [commandId, {name:value, name2:value2 ... }].toJSON()}).toJSON());</code>
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
	@SuppressWarnings("rawtypes")
	@ControllerAction
	public Object execute(Object attributes)
	{
		String commandId = getCommandId(attributes);
		if (commandId == null)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		// At this point we can tell that the command-arguments format is OK, so we can just ask for the optional
		// arguments.
		Map arguments = getCommandArguments(attributes);
		try
		{
			ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(
					ICommandService.class);
			IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
					IHandlerService.class);
			Command command = commandService.getCommand(commandId);
			if (command == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, PortalUIPlugin.PLUGIN_ID, "Command not found - " //$NON-NLS-1$
						+ commandId));
			}
			ExecutionEvent event = null;
			if (arguments == null)
			{
				arguments = Collections.emptyMap();
			}
			event = new ExecutionEvent(command, arguments, null, handlerService.getCurrentState());
			command.executeWithChecks(event);
		}
		catch (Exception e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
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
			if ((arr.length == 1 || arr.length == 2) && arr[0] != null)
			{
				return arr[0].toString();
			}
			else
			{
				String message = "Wrong argument count passed to CommandHandlerActionController::execute. Expected 1 or 2 and got " + arr.length; //$NON-NLS-1$
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
			}
		}
		else
		{
			String message = "Wrong argument type passed to CommandHandlerActionController::execute. Expected Object[] and got " //$NON-NLS-1$
					+ ((attributes == null) ? "null" : attributes.getClass().getName()); //$NON-NLS-1$
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
		}
		return null;
	}

	/**
	 * @param attributes
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Map getCommandArguments(Object attributes)
	{
		Object[] arr = (Object[]) attributes;
		if (arr.length == 2)
		{
			if (arr[1] instanceof Map)
			{
				return (Map) arr[1];
			}
			else
			{
				String message = "Wrong argument type passed as command-arguments to CommandHandlerActionController::execute. Expected 'Map' and got " //$NON-NLS-1$
						+ arr[1].getClass().getName();
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
			}
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
