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
package com.aptana.commandline.launcher.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.aptana.commandline.launcher.CommandlineArgumentsHandler;
import com.aptana.commandline.launcher.CommandlineLauncherPlugin;
import com.aptana.commandline.launcher.server.port.PortManager;

/**
 * @author schitale
 */
public class LauncherServer
{

	/**
	 * Start the server that listens on a socket for command line arguments.
	 */
	public static void startServer()
	{
		try
		{
			CommandLineArgsServer server = new CommandLineArgsServer();
			server.start();
		}
		catch (IOException e)
		{
			CommandlineLauncherPlugin.logError(e);
		}
	}

	/**
	 * CommandLineArgsServer
	 */
	static class CommandLineArgsServer extends Thread
	{
		/**
		 * STARTING_PORT
		 */
		public static final int STARTING_PORT = 9980;

		ServerSocket server = null;
		String line;
		DataInputStream is;
		PrintStream os;

		/**
		 * CommandLineArgsServer
		 *
		 * @param helper
		 * @throws IOException
		 */
		public CommandLineArgsServer() throws IOException
		{
			super("CommandLineArgsServer"); //$NON-NLS-1$

			int port = getPort();
			PortManager.writeCurrentPort(port);
		}

		/**
		 * getPort
		 *
		 * @return int
		 */
		public int getPort()
		{
			int tries = 10;
			int port = STARTING_PORT;

			while (tries > 0)
			{
				try
				{
					server = new ServerSocket(port, 0, null);
					return port;
				}
				catch (IOException e)
				{
					tries--;
					port++;
				}
			}

			return -1;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			if (server == null)
			{
				return;
			}

			while (server.isClosed() == false)
			{
				Socket clientSocket = null;
				try
				{
					clientSocket = server.accept();

					BufferedReader r = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					BufferedWriter w = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

					List<String> args = new LinkedList<String>();
					while ((line = r.readLine()) != null)
					{
						args.add(line);
					}
					if (args.size() > 0)
					{
						// Process command line arguments
						CommandlineArgumentsHandler.processCommandLineArgs(args.toArray(new String[0]));
					}

					// Send ACK
					w.write("pong"); //$NON-NLS-1$
					w.flush();
				}
				catch (IOException e)
				{
					CommandlineLauncherPlugin.logError(e);
				}
				finally
				{
					if (clientSocket != null)
					{
						try
						{
							clientSocket.close();
						}
						catch (IOException e)
						{
							// Ignore
						}
					}
				}
			}
		}
	}

}
