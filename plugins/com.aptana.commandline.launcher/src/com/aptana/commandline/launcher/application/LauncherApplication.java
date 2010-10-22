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
package com.aptana.commandline.launcher.application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.util.NLS;

import com.aptana.commandline.launcher.CommandlineLauncherPlugin;
import com.aptana.commandline.launcher.server.port.PortManager;

/**
 * This application is used to launch Aptana IDE from a command line.
 * 
 * @author schitale
 *
 */
public class LauncherApplication implements IApplication
{

	private IApplication productApplication;

	public Object start(IApplicationContext context) throws Exception
	{
		int port = -1;
		try
		{
			// Is an there any other instance running ?
			port = PortManager.getRunningInstancePort();
			if (port < 0)
			{
				// Another instance is not running; launch the Product's application
				String productApplicationId = Platform.getProduct().getApplication();
				productApplication = getApplication(productApplicationId);
				if (productApplication != null)
				{
					return productApplication.start(context);
				}
				else
				{
					CommandlineLauncherPlugin.logError(new IllegalStateException(NLS.bind(
							Messages.LauncherApplication_ApplicationNotFound, productApplicationId)));
				}
			}
			else
			{
				// Get application args
				Object args = context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
				if (args instanceof String[])
				{
					final String[] arguments = (String[]) args;
					if (arguments.length > 0)
					{						
						// Send the command line arguments to the running instance
						if (!sendArguments(port, arguments))
						{
							CommandlineLauncherPlugin.logError(new IllegalStateException(
									Messages.LauncherApplication_CouldNotSendCommandLineArguments));
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			// Log any other exception
			CommandlineLauncherPlugin.logError(ex);
		}
		return EXIT_OK;
	}

	public void stop()
	{
		if (productApplication != null)
		{
			productApplication.stop();
		}
	}

	/*
	 * return the application to run, or null if not even the default application is found.
	 */
	private IApplication getApplication(String applicationId) throws CoreException
	{
		IExtension extension = Platform.getExtensionRegistry().getExtension(Platform.PI_RUNTIME,
				Platform.PT_APPLICATIONS, applicationId);

		// Return the application object for specified applicationId
		IConfigurationElement[] elements = extension.getConfigurationElements();
		if (elements.length > 0)
		{
			IConfigurationElement[] runs = elements[0].getChildren("run"); //$NON-NLS-1$
			if (runs.length > 0)
			{
				Object runnable = runs[0].createExecutableExtension("class"); //$NON-NLS-1$
				if (runnable instanceof IApplication)
					return (IApplication) runnable;
			}
		}
		return null;
	}

	public static boolean sendArguments(int port, String[] arguments)
	{
		// Convert to absolute paths
		List<String> filesList = new LinkedList<String>();
		for (String argument : arguments)
		{
			File file = new File(argument);
			if (file.exists()) {
				filesList.add(file.getAbsolutePath());
			}
		}
		arguments = filesList.toArray(new String[0]);

		Socket socket = null;
		InputStream is = null;
		OutputStream os = null;

		try
		{
			// Open the socket to the port
			socket = new Socket(InetAddress.getByName(null), port);
			os = socket.getOutputStream();
			is = socket.getInputStream();
		}
		catch (UnknownHostException e)
		{
			CommandlineLauncherPlugin.logError(e);
			return false;
		}
		catch (IOException e)
		{
			CommandlineLauncherPlugin.logError(e);
			return false;
		}

		if (os != null)
		{
			try
			{
				PrintWriter ps = new PrintWriter(os);
				for (String argument : arguments)
				{
					ps.println(argument);
				}
				ps.flush();
				ps.close();
				try
				{
					// Read the ACK
					is.read();
				}
				catch (IOException e)
				{
					// Ignore
				}
				return true;
			}
			finally
			{
				try
				{
					socket.close();
				}
				catch (IOException e)
				{
					// Ignore
				}
			}
		}

		return false;
	}

}
