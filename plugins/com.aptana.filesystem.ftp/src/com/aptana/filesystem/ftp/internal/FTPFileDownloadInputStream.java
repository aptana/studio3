/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.ftp.internal;

import java.io.IOException;
import java.io.InputStream;

import com.enterprisedt.net.ftp.FTPClientInterface;
import com.enterprisedt.net.ftp.FileTransferInputStream;

/**
 * @author Max Stepanov
 *
 */
public class FTPFileDownloadInputStream extends InputStream {

	private FTPClientInterface ftpClient;
	private FileTransferInputStream ftpInputStream;
	private FTPClientPool pool;
	
	/**
	 * @param pool 
	 * 
	 */
	public FTPFileDownloadInputStream(FTPClientPool pool, FTPClientInterface ftpClient, FileTransferInputStream ftpInputStream) {
		this.ftpClient = ftpClient;
		this.pool = pool;
		this.ftpInputStream = ftpInputStream;
	}
	
	private void safeQuit() {
		try
		{
			ftpInputStream.close();
		}
		catch (IOException e)
		{
			// ignore
		}
		pool.checkIn(ftpClient);
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		try {
			return ftpInputStream.read();
		} catch (IOException e) {
			safeQuit();
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		try {
			return ftpInputStream.available();
		} catch (IOException e) {
			safeQuit();
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			ftpInputStream.close();
		} finally {
			safeQuit();
		}
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		try {
			return ftpInputStream.read(b, off, len);
		} catch (IOException e) {
			safeQuit();
			throw e;
		}
	}

}
