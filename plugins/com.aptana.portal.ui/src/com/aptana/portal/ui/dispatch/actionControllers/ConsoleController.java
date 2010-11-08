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

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.mortbay.util.ajax.JSON;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.portal.ui.PortalUIPlugin;
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
		revealConsole(console);

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

	private void revealConsole(MessageConsole console)
	{
		IWorkbenchWindow workbenchWindow = PortalUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null)
		{
			IWorkbenchPage activePage = workbenchWindow.getActivePage();
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view;
			try
			{
				view = (IConsoleView) activePage.showView(id);
				view.display(console);
			}
			catch (PartInitException e)
			{
				PortalUIPlugin.logError(e);
			}
		}

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
