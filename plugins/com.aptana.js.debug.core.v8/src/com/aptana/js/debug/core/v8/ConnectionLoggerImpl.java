/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.js.debug.core.v8;

import java.io.PrintStream;

import org.chromium.sdk.ConnectionLogger;

/**
 * @author Max Stepanov
 *
 */
public class ConnectionLoggerImpl implements ConnectionLogger {

	private final PrintStream stream;
	
	/**
	 * 
	 */
	public ConnectionLoggerImpl(PrintStream stream) {
		this.stream = stream;
	}

	/* (non-Javadoc)
	 * @see org.chromium.sdk.ConnectionLogger#getIncomingStreamListener()
	 */
	public StreamListener getIncomingStreamListener() {
		return new StreamListenerImpl();
	}

	/* (non-Javadoc)
	 * @see org.chromium.sdk.ConnectionLogger#getOutgoingStreamListener()
	 */
	public StreamListener getOutgoingStreamListener() {
		return new StreamListenerImpl();
	}

	/* (non-Javadoc)
	 * @see org.chromium.sdk.ConnectionLogger#setConnectionCloser(org.chromium.sdk.ConnectionLogger.ConnectionCloser)
	 */
	public void setConnectionCloser(ConnectionCloser connectionCloser) {
	}

	/* (non-Javadoc)
	 * @see org.chromium.sdk.ConnectionLogger#start()
	 */
	public void start() {
	}

	/* (non-Javadoc)
	 * @see org.chromium.sdk.ConnectionLogger#handleEos()
	 */
	public void handleEos() {
	}
	
	private class StreamListenerImpl implements StreamListener {

		public void addContent(CharSequence text) {
			stream.println(text);
		}

		public void addSeparator() {
			stream.println();
		}
		
	}
}


