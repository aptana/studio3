/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * A controller the tunnels console.log calls to the Studio's console.<br>
 * Note that regular log messages will only work when Platform.inDevelopmentMode() is true. While error logging will be
 * active regardless of the development mode.
 * 
 * @author shalom
 */
public class ConsoleController extends AbstractActionController
{

	private static final String BROWSER_CONSOLE_NAME = Messages.ConsoleController_devToolboxConsoleName;

	public ConsoleController()
	{
	}

	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
	}

	// ############## Actions ###############

	@ControllerAction
	public Object log(Object arguments)
	{
		if (Platform.inDevelopmentMode())
		{
			writeToConsole(arguments, false);
		}
		return IBrowserNotificationConstants.JSON_OK;
	}

	@ControllerAction
	public Object debug(Object arguments)
	{
		return log(arguments);
	}

	@ControllerAction
	public Object error(Object arguments)
	{
		writeToConsole(arguments, true);
		return IBrowserNotificationConstants.JSON_OK;
	}

	private void writeToConsole(Object arguments, boolean isError)
	{
		MessageConsole console = findOrCreateConsole();

		MessageConsoleStream out = console.newMessageStream();
		if (isError)
		{
			out.setColor(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		}
		else
		{
			out.setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		}
		out.println(JSON.toString(arguments));
	}

	private MessageConsole findOrCreateConsole()
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		for (IConsole console : conMan.getConsoles())
		{
			if (BROWSER_CONSOLE_NAME.equals(console.getName()))
			{
				return (MessageConsole) console;
			}
		}
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(BROWSER_CONSOLE_NAME, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
}
