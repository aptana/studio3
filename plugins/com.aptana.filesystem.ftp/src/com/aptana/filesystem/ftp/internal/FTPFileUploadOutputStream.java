/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
