/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.debug.core.DebugException;

import com.aptana.js.debug.core.internal.ProtocolLogger;

public interface IJSConnection
{
	/**
	 * Initialize this connection with a socket and a protocol logger.
	 * 
	 * @param socket
	 *            A {@link Socket}
	 * @param logger
	 *            A {@link ProtocolLogger} (can be <code>null</code>)
	 * @throws IOException
	 */
	void initialize(Socket socket, ProtocolLogger logger) throws IOException;

	Socket getSocket();

	boolean isConnected();

	void sendCommand(String command) throws DebugException;

	String[] sendCommandAndWait(String command) throws DebugException;

	boolean isTerminated();

	void start(IJSDebugConnectionHandler connectionHandler);

	void stop();

	void dispose() throws IOException;
}
