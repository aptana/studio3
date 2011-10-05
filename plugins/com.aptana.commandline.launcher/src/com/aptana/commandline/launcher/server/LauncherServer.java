/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
import com.aptana.core.logging.IdeLog;

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
			IdeLog.logError(CommandlineLauncherPlugin.getDefault(), e);
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
					IdeLog.logError(CommandlineLauncherPlugin.getDefault(), e);
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
