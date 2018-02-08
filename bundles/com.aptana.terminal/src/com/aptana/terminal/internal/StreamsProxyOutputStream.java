/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.terminal.internal;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.debug.core.model.IStreamsProxy;

/**
 * @author Max Stepanov
 */
public class StreamsProxyOutputStream extends OutputStream {

	private IStreamsProxy streamsProxy;
	private String encoding;

	/**
	 * 
	 */
	public StreamsProxyOutputStream(IStreamsProxy streamsProxy, String encoding) {
		this.streamsProxy = streamsProxy;
		this.encoding = encoding;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		streamsProxy.write(new String(new byte[] { (byte) (b & 0xFF) }, encoding));
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		streamsProxy.write(new String(b, off, len, encoding));
	}

}
