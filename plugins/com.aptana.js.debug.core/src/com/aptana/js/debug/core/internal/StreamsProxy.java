/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;


/**
 * @author Max Stepanov
 */
public class StreamsProxy implements IStreamsProxy {
	
	private final OutputStreamMonitor outMonitor;
	private final OutputStreamMonitor errMonitor;

	/**
	 * StreamsProxy
	 * 
	 * @param outIn
	 * @param errIn
	 */
	public StreamsProxy(InputStream outIn, InputStream errIn) {
		outMonitor = new OutputStreamMonitor(outIn, null);
		errMonitor = new OutputStreamMonitor(errIn, null);
		outMonitor.startMonitoring();
		errMonitor.startMonitoring();
	}

	/*
	 * @see org.eclipse.debug.core.model.IStreamsProxy#getErrorStreamMonitor()
	 */
	public IStreamMonitor getErrorStreamMonitor() {
		return errMonitor;
	}

	/*
	 * @see org.eclipse.debug.core.model.IStreamsProxy#getOutputStreamMonitor()
	 */
	public IStreamMonitor getOutputStreamMonitor() {
		return outMonitor;
	}

	/*
	 * @see org.eclipse.debug.core.model.IStreamsProxy#write(java.lang.String)
	 */
	public void write(String input) throws IOException {
		throw new IOException("not supported"); //$NON-NLS-1$
	}
}
