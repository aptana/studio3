/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.js.debug.core.v8;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.core.runtime.CoreException;

import com.aptana.debug.core.util.DebugUtil;
import com.aptana.js.debug.core.internal.ProtocolLogger;
import com.aptana.js.debug.core.internal.model.DebugConnection;

/**
 * @author Max Stepanov
 *
 */
public class V8DebugConnection extends DebugConnection {

	public static DebugConnection createConnection(V8DebugHost debugHost, ProtocolLogger logger) throws CoreException {
		try {
			ServerSocket listenSocket = DebugUtil.allocateServerSocket(0);
			debugHost.start(listenSocket.getLocalSocketAddress());
			Socket socket = listenSocket.accept();
			if (socket != null) {
				socket.setSoTimeout(V8DebugHost.SOCKET_TIMEOUT);
				return new V8DebugConnection(socket,
						new InputStreamReader(socket.getInputStream()),
						new OutputStreamWriter(socket.getOutputStream()),
						logger);
			}
		}
		catch (IOException e) {
			throwDebugException(e);
		}
		return null;
	}
	
	/**
	 * @param reader
	 * @param writer
	 */
	private V8DebugConnection(Socket socket, Reader reader, Writer writer, ProtocolLogger logger) {
		super(socket, reader, writer, logger);
	}

}
