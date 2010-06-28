/*******************************************************************************
 * Copyright (c) 2007 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Michael Scharf (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.emulator;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.tm.internal.terminal.provisional.api.Logger;

public class LoggingOutputStream extends FilterOutputStream {

	public LoggingOutputStream(OutputStream out) {
		super(out);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		if(Logger.isLogEnabled())
			Logger.log("Received " + len + " bytes: '" + //$NON-NLS-1$ //$NON-NLS-2$
					Logger.encode(new String(b, 0, len)) + "'"); //$NON-NLS-1$
		
		// we cannot call super.write, because this would call our write
		// which logs character by character.....
		//super.write(b, off, len);
		if ((off | len | (b.length - (len + off)) | (off + len)) < 0)
		    throw new IndexOutOfBoundsException();

		for (int i = 0 ; i < len ; i++) {
		    super.write(b[off + i]);
		}
	}

	public void write(int b) throws IOException {
		if(Logger.isLogEnabled())
			Logger.log("Received " + 1 + " bytes: '" + //$NON-NLS-1$ //$NON-NLS-2$
					Logger.encode(new String(new byte[]{(byte)b}, 0, 1)) + "'"); //$NON-NLS-1$
		super.write(b);
	}

}
