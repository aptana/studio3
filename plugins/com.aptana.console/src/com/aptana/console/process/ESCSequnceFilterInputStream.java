/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable questionableAssignment
// $codepro.audit.disable exceptionUsage.exceptionCreation

package com.aptana.console.process;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Max Stepanov
 *
 */
public class ESCSequnceFilterInputStream extends FilterInputStream {

	/**
	 * @param in
	 */
	public ESCSequnceFilterInputStream(InputStream in) {
		super(in);
	}

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int ch =  super.read();
		if (ch == 27) {
			ch = super.read();
			if (ch == '[') {
				ch = super.read();
			}
			
			while (!( ch >= 64 && ch <= 126)) {
				if (ch < 0) {
					return ch; 
				}
				ch = super.read();
			}
			ch = super.read();
		}
		return ch;
	}

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
		    throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
		    throw new IndexOutOfBoundsException();
		} else if (len == 0) {
		    return 0;
		}

		int c = read();
		if (c == -1) {
		    return -1;
		}
		b[off] = (byte)c;

		int i = 1;
		try {
		    for (; i < len ; i++) {
			c = read();
			if (c == -1) {
			    break;
			}
			b[off + i] = (byte)c;
		    }
		} catch (IOException e) {
			e.getCause();
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

}
