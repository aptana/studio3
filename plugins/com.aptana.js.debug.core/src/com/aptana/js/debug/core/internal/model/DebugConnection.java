/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;

import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.internal.ProtocolLogger;
import com.aptana.js.debug.core.model.IJSConnection;
import com.aptana.js.debug.core.model.IJSDebugConnectionHandler;

/**
 * @author Max Stepanov
 */
public class DebugConnection implements IJSConnection
{

	/**
	 * COMMAND_TIMEOUT
	 */
	protected static final int COMMAND_TIMEOUT = 20000;

	private static final String ARGS_SPLIT = "\\*"; //$NON-NLS-1$

	private Socket socket;
	private Reader reader;
	private Writer writer;
	private boolean connected = false;
	private boolean terminated = false;
	private ProtocolLogger logger;

	private Map<String, Object> locks = new Hashtable<String, Object>(); // synchronized get/put required
	private volatile long lastReqId = System.currentTimeMillis();

	private IJSDebugConnectionHandler handler;

	/**
	 * @throws DebugException
	 */
	public static DebugConnection createConnection(Socket socket) throws DebugException
	{
		return createConnection(socket, null);
	}

	/**
	 * @throws DebugException
	 */
	public static DebugConnection createConnection(Socket socket, ProtocolLogger logger) throws DebugException
	{
		try
		{
			return new DebugConnection(socket, new InputStreamReader(socket.getInputStream()), new OutputStreamWriter(
					socket.getOutputStream()), logger);
		}
		catch (IOException e)
		{
			throwDebugException(e);
			return null;
		}
	}

	/**
	 * Constructs a new Debug Connection. Note that {@link #initialize(Socket, ProtocolLogger)} has to be called after.
	 */
	public DebugConnection()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.js.debug.core.model.IJSConnection#initialize(java.net.Socket,
	 * com.aptana.js.debug.core.internal.ProtocolLogger, org.eclipse.debug.core.ILaunch)
	 */
	public void initialize(Socket socket, ProtocolLogger logger, ILaunch launch) throws IOException
	{
		this.socket = socket;
		this.reader = new InputStreamReader(socket.getInputStream());
		this.writer = new OutputStreamWriter(socket.getOutputStream());
		this.logger = logger;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.js.debug.core.model.IJSConnection#initialize(java.net.InetSocketAddress,
	 * com.aptana.js.debug.core.internal.ProtocolLogger, org.eclipse.debug.core.ILaunch)
	 */
	public void initialize(InetSocketAddress inetSocketAddress, ProtocolLogger logger, ILaunch launch)
			throws IOException
	{
		Socket socket = new Socket();
		socket.connect(inetSocketAddress);
		initialize(socket, logger, launch);
	}

	protected DebugConnection(Socket socket, Reader reader, Writer writer, ProtocolLogger logger)
	{
		this.socket = socket;
		this.reader = reader;
		this.writer = writer;
		this.logger = logger;
	}

	public void start(IJSDebugConnectionHandler handler)
	{
		this.handler = handler;

		connected = true;
		new Thread("Aptana: JS Debugger") { //$NON-NLS-1$
			public void run()
			{
				String message;
				while ((socket != null && !socket.isClosed()) || reader != null)
				{
					try
					{
						try
						{
							message = readMessage();
						}
						catch (SocketTimeoutException ste)
						{
							// This is expected, since we may set the socket timeout to a short interval.
							// See https://jira.appcelerator.org/browse/TISTUD-1431
							continue;
						}
						if (message == null)
						{
							break;
						}
						if (logger != null)
						{
							logger.log(true, message);
						}
						handleMessage(message);
					}
					catch (SocketException e)
					{
						break;
					}
					catch (Exception e)
					{
						JSDebugPlugin.log(e);
					}
				}
				if (logger != null)
				{
					logger.close();
				}
				handleConnectionTerminated();
			}

		}.start();
	}

	public void stop()
	{
		if (!connected)
		{
			return;
		}
		connected = false;
		synchronized (locks)
		{
			Object[] list = locks.values().toArray();
			locks.clear();
			for (Object lock : list)
			{
				synchronized (lock)
				{
					lock.notify();
				}
			}
		}

	}

	public void dispose() throws IOException
	{
		if (reader != null)
		{
			reader.close();
			writer.close();
			reader = null;
			writer = null;
		}
		if (socket != null)
		{
			socket.close();
			socket = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.js.debug.core.model.IJSDebugConnection#getSocket()
	 */
	public Socket getSocket()
	{
		return socket;
	}

	private void handleMessage(String message)
	{
		if (message.endsWith("*")) { //$NON-NLS-1$
			message += "* "; //$NON-NLS-1$
		}
		handler.handleMessage(message);

		if (!connected)
		{
			return;
		}
		/* check if action comes to waiting commands */
		String[] args = message.split(ARGS_SPLIT);
		String action = args[0];
		Object lock = locks.get(action);
		if (lock != null)
		{
			locks.put(action, args);
			synchronized (lock)
			{
				lock.notify();
			}
			return;
		}
	}

	/**
	 * handleConnectionTerminated
	 */
	private void handleConnectionTerminated()
	{
		if (terminated)
		{
			return;
		}
		terminated = true;
		handler.handleShutdown();
	}

	public boolean isConnected()
	{
		return connected;
	}

	public boolean isTerminated()
	{
		return terminated;
	}

	/**
	 * Send command w/o waiting for response
	 * 
	 * @param command
	 * @throws DebugException
	 */
	public void sendCommand(String command) throws DebugException
	{
		sendCommand("", command); //$NON-NLS-1$
	}

	/**
	 * Send command w/o waiting for response
	 * 
	 * @param reqid
	 * @param command
	 * @throws DebugException
	 */
	protected void sendCommand(String reqid, String command) throws DebugException
	{
		try
		{
			String message = MessageFormat.format("{0}*{1}*{2}", //$NON-NLS-1$
					Integer.toString(command.length() + reqid.length() + 1), reqid, command);
			if (logger != null)
			{
				logger.log(false, message);
			}
			writer.write(message);
			writer.flush();
		}
		catch (IOException e)
		{
			throwDebugException(e);
		}
	}

	/**
	 * Send command and wait for response
	 * 
	 * @param command
	 * @return String[]
	 * @throws DebugException
	 */
	public String[] sendCommandAndWait(String command) throws DebugException
	{
		long reqid = ++lastReqId;
		return sendCommandAndWait(command, Long.toString(reqid));
	}

	/**
	 * Send command and wait for response
	 * 
	 * @param command
	 * @param reqid
	 * @return String[]
	 * @throws DebugException
	 */
	protected String[] sendCommandAndWait(String command, String reqid) throws DebugException
	{
		if (!connected)
		{
			return null;
		}
		Object lock = new Object();
		synchronized (lock)
		{
			try
			{
				locks.put(reqid, lock);
				sendCommand(reqid, command);
				lock.wait(COMMAND_TIMEOUT);
			}
			catch (InterruptedException e)
			{
				throwDebugException(e);
			}
		}
		lock = locks.remove(reqid);
		if (lock instanceof String[])
		{
			return (String[]) lock;
		}
		return null;
	}

	/**
	 * readMessage
	 * 
	 * @return String
	 * @throws IOException
	 */
	protected String readMessage() throws IOException
	{
		StringBuffer sb = new StringBuffer();
		int messageSize = 0;
		int i;
		char ch;
		while ((i = reader.read()) != -1)
		{
			ch = (char) i;
			if (ch == '*' && sb.length() > 0)
			{
				try
				{
					messageSize = Integer.parseInt(sb.toString());
					break;
				}
				catch (NumberFormatException e)
				{
				}
				sb.setLength(0);
			}
			else if (ch >= '0' && ch <= '9')
			{
				sb.append(ch);
			}
			else if (sb.length() > 0)
			{
				sb.setLength(0);
			}
		}
		if (i == -1)
		{
			return null;
		}

		char[] buffer = new char[1024];
		sb.setLength(0); // clear the buffer
		int n;
		while (messageSize > sb.length())
		{
			n = reader.read(buffer, 0, Math.min(messageSize - sb.length(), buffer.length));
			if (n == -1)
			{
				return null;
			}
			sb.append(buffer, 0, n);
		}
		return sb.toString();
	}

	/**
	 * throwDebugException
	 * 
	 * @param exception
	 * @throws DebugException
	 */
	protected static void throwDebugException(Exception exception) throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, JSDebugPlugin.PLUGIN_ID,
				DebugException.TARGET_REQUEST_FAILED, "", exception)); //$NON-NLS-1$
	}

}
