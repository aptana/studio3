/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;

import com.aptana.js.debug.core.internal.ProtocolLogger;

/**
 * Represents a socket connection to a JavaScript application. This can be a debug connection, profile connection and
 * such. See the "jsConnectionInitializers" extension point.
 * 
 * @author sgibly@appcelerator.com.
 */
public interface IJSConnection
{
	/**
	 * Initialize this connection with a socket and a protocol logger. This method may block until a connection is made,
	 * or until it timeout. In case of a timeout, an exception will be thrown to indicate a failure.
	 * 
	 * @param socket
	 *            A {@link Socket}
	 * @param logger
	 *            A {@link ProtocolLogger} (can be <code>null</code>)
	 * @param launch
	 *            An {@link ILaunch} (can be <code>null</code>)
	 * @throws IOException
	 */
	void initialize(Socket socket, ProtocolLogger logger, ILaunch launch) throws IOException;

	/**
	 * Initialize this connection with an {@link InetSocketAddress} and a protocol logger. This method may block until a
	 * connection is made, or until it timeout. In case of a timeout, an exception will be thrown to indicate a failure.
	 * 
	 * @param inetSocketAddress
	 *            An {@link InetSocketAddress}
	 * @param logger
	 *            A {@link ProtocolLogger} (can be <code>null</code>)
	 * @param launch
	 *            An {@link ILaunch} (can be <code>null</code>)
	 * @throws IOException
	 */
	void initialize(InetSocketAddress inetSocketAddress, ProtocolLogger logger, ILaunch launch) throws IOException;

	/**
	 * Returns the connection's {@link Socket}.
	 * 
	 * @return A {@link Socket}
	 */
	Socket getSocket();

	/**
	 * Returns if this connection is alive (e.g. connected).
	 * 
	 * @return <code>true</code> if the connection is alive; <code>false</code> otherwise.
	 */
	boolean isConnected();

	/**
	 * Sends a string command via the connection's output stream.
	 * 
	 * @param command
	 * @throws DebugException
	 */
	void sendCommand(String command) throws DebugException;

	/**
	 * Sends a string command and wait for response via the connection's output stream.
	 * 
	 * @param command
	 * @return
	 * @throws DebugException
	 */
	String[] sendCommandAndWait(String command) throws DebugException;

	/**
	 * Returns if this connection was terminated.s
	 * 
	 * @return <code>true</code> if it was terminated; <code>false</code> otherwise.s
	 */
	boolean isTerminated();

	/**
	 * Starts the connection with a given handler.s
	 * 
	 * @param connectionHandler
	 */
	void start(IJSDebugConnectionHandler connectionHandler);

	/**
	 * Stop the connection.
	 */
	void stop();

	/**
	 * Dispose any resources that were held in the connection instance.
	 * 
	 * @throws IOException
	 */
	void dispose() throws IOException;

}
