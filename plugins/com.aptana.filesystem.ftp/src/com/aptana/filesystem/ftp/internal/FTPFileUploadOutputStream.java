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
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Date;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPClientInterface;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferOutputStream;

/**
 * @author Max Stepanov
 *
 */
public class FTPFileUploadOutputStream extends OutputStream {
	
	private FTPClientInterface ftpClient;
	private FileTransferOutputStream ftpOutputStream;
	private String filename;
	private Date modificationTime;
	private long permissions;
	private FTPClientPool pool;
	private Runnable completeRunnable;
	
	/**
	 * @param pool 
	 * 
	 */
	public FTPFileUploadOutputStream(FTPClientPool pool, FTPClientInterface ftpClient, FileTransferOutputStream ftpOutputStream, String filename, Date modificationTime, long permissions, Runnable completeRunnable) {
		this.ftpClient = ftpClient;
		this.ftpOutputStream = ftpOutputStream;
		this.filename = filename;
		this.modificationTime = modificationTime;
		this.permissions = permissions;
		this.pool = pool;
		this.completeRunnable = completeRunnable;
	}

	private void safeQuit(boolean failed) {		
		try {
			if (ftpClient.connected()) {
				if (failed && filename != null) {
					ftpClient.delete(ftpOutputStream.getRemoteFile());
				}
			}
		} catch (Exception ignore) {
		} finally {
			try {
				ftpOutputStream.close();
			} catch (IOException ignore) {
			}
			pool.checkIn(ftpClient);
			if (completeRunnable != null) {
				completeRunnable.run();
				completeRunnable = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		try {
			ftpOutputStream.write(b);
		} catch (IOException e) {
			safeQuit(true);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			ftpOutputStream.close();
			try {
				String actualFilename = filename != null ? filename : ftpOutputStream.getRemoteFile();
				if (filename != null) {
					if (ftpClient.exists(filename)) {
						ftpClient.delete(filename);
					}
					ftpClient.rename(ftpOutputStream.getRemoteFile(), actualFilename);
					filename = null;
				}
				if (modificationTime != null) {
					ftpClient.setModTime(actualFilename, modificationTime);
				}
                if (ftpClient instanceof FTPClient && permissions > 0) {
                    ((FTPClient) ftpClient).site(
                    		MessageFormat.format("CHMOD {0} {1}", Long.toOctalString(permissions), actualFilename)); //$NON-NLS-1$
                }
			} catch (FTPException e) {
				safeQuit(true);
				throw new IOException(e.getMessage()); 
			}
		} finally {
			safeQuit(false);
		}
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		try {
			ftpOutputStream.write(b, off, len);
		} catch (IOException e) {
			safeQuit(true);
			throw e;
		}		
	}

}
