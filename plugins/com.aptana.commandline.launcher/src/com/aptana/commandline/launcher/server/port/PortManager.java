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
package com.aptana.commandline.launcher.server.port;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import com.aptana.commandline.launcher.CommandlineLauncherPlugin;

/**
 * This class manages the file used to record the Command line server port.
 *
 * @author schitale
 *
 */
public class PortManager
{
	private static String dotAptanaFile = null;

	/**
	 * getRunningInstancePort
	 *
	 * @return the port number the command line server is running on, or -1 if there is no server running
	 */
	public static int getRunningInstancePort()
	{
		int port = readCurrentPort();
		// If the .aptana file did not exist or contained a bogus port number
		// simply return
		if (port < 0)
		{
			return port;
		}
		// Now check if the port is actually in use
		ServerSocket serverSocket = null;
		try
		{
			serverSocket = new ServerSocket(port, 0, null);
			// Socket not in use. Assume that the .aptana file was left over from
			// a abnormal exit the last time.
			return -1;
		}
		catch (BindException e)
		{
			// Address in use. Assume that the port is in use by a running instance of the IDE.
		}
		catch (IOException e)
		{
			CommandlineLauncherPlugin.logInfo(e.getLocalizedMessage(), e);
		}
		finally
		{
			if (serverSocket != null)
			{
				try
				{
					serverSocket.close();
				}
				catch (IOException e)
				{
					// Ignore
				}
			}
		}
		return port;
	}

	/**
	 * Remove the port file.
	 *
	 */
	public static void doShutdownCleanup()
	{
		if (dotAptanaFile != null)
		{
			File file = new File(dotAptanaFile);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * Write the current port to the .aptana file.
	 *
	 * @param port
	 */
	public static void writeCurrentPort(int port)
	{
		if (dotAptanaFile == null)
		{
			dotAptanaFile = computeDotAptanaFileName();
			if (dotAptanaFile == null)
			{
				return;
			}
		}

		FileWriter fileWriter = null;
		try
		{
			File file = new File(dotAptanaFile);
			fileWriter = new FileWriter(dotAptanaFile);
			PrintWriter pw = new PrintWriter(fileWriter);
			pw.println(String.valueOf(port));
			pw.flush();

			if (file.exists())
			{
				// Mark for deletion
				file.deleteOnExit();
			}
		}
		catch (IOException e)
		{
			CommandlineLauncherPlugin.logInfo(e.getLocalizedMessage(), e);
			return;
		}
		finally
		{
			if (fileWriter != null)
			{
				try
				{
					fileWriter.close();
				}
				catch (IOException e)
				{
					// Ignore
				}
			}
		}
	}

	/**
	 * Return the port number recorded in the .aptana file. Returns
	 * -1 if the .aptana file does not exist or is corrupt.
	 *
	 * @return the port number
	 */
	private static int readCurrentPort()
	{

		if (dotAptanaFile == null)
		{
			dotAptanaFile = computeDotAptanaFileName();
			if (dotAptanaFile == null)
			{
				return -1;
			}
		}

		FileReader fileReader = null;
		try
		{
			fileReader = new FileReader(dotAptanaFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String sPort = bufferedReader.readLine().trim();
			if (sPort.length() == 0)
			{
				// Corrupt file ?
				return -1;
			}

			// Return the port number
			return Integer.parseInt(sPort);
		}
		catch (NumberFormatException e)
		{
			// Corrupt .aptana file
			return -1;
		}
		catch (FileNotFoundException e)
		{
			// .aptana file does not exist
			return -1;
		}
		catch (IOException e)
		{
			CommandlineLauncherPlugin.logError(e);
			return -1;
		}
		finally
		{
			if (fileReader != null)
			{
				try
				{
					fileReader.close();
				}
				catch (IOException e)
				{
					// Ignore
				}
			}
		}
	}

	private static String computeDotAptanaFileName()
	{
		// Attempt to locate .aptana file in a predicatable place
		// Use user's home folder as the default if any other
		// locations below are not writable.
		File dotAptanaParent = new File(System.getProperty("user.home")); //$NON-NLS-1$

		// Prefer install location
		Location location = Platform.getInstallLocation();
		if (location == null || location.isReadOnly())
		{
			// If install location is null (?) or read only - try configuration location
			location = Platform.getConfigurationLocation();
			if (location == null || location.isReadOnly())
			{
				// If configuration location is null (?) or read only - try user location
				location = Platform.getUserLocation();
				if (location == null || location.isReadOnly())
				{
					// If user location is null or read-only - too bad.
					location = null;
				}
			}
		}
		if (location != null)
		{
			URL locationURL = location.getURL();
			if (locationURL != null && "file".equals(locationURL.getProtocol())) { //$NON-NLS-1$
				try
				{
					dotAptanaParent = new File(locationURL.toURI());
				}
				catch (URISyntaxException e)
				{
					dotAptanaParent = new File(locationURL.getPath());
				}
			}
		}

		File computedDotAptanaFile = new File(dotAptanaParent, ".aptana"); //$NON-NLS-1$
		return computedDotAptanaFile.getAbsolutePath();
	}
}
