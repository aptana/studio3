/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
import com.aptana.core.logging.IdeLog;

/**
 * This application is used to launch Aptana IDE from a command line.
 * 
 * @author schitale
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
					Exception e = new IllegalStateException(NLS.bind(Messages.LauncherApplication_ApplicationNotFound,
							productApplicationId));
					IdeLog.logError(CommandlineLauncherPlugin.getDefault(), e);
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
							Exception e = new IllegalStateException(
									Messages.LauncherApplication_CouldNotSendCommandLineArguments);
							IdeLog.logError(CommandlineLauncherPlugin.getDefault(), e);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CommandlineLauncherPlugin.getDefault(), e);
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
			if (file.exists())
			{
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
		catch (IOException e)
		{
			IdeLog.logError(CommandlineLauncherPlugin.getDefault(), e);
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
