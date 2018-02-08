/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeInFinally

package com.aptana.filesystem.ftp.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Date;

import com.aptana.core.util.ProgressMonitorInterrupter;
import com.aptana.core.util.ProgressMonitorInterrupter.InterruptDelegate;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPClientInterface;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferOutputStream;

/**
 * @author Max Stepanov
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
	 */
	public FTPFileUploadOutputStream(FTPClientPool pool, FTPClientInterface _ftpClient, FileTransferOutputStream ftpOutputStream, String filename, Date modificationTime, long permissions, Runnable completeRunnable) {
		this.ftpClient = _ftpClient;
		this.ftpOutputStream = ftpOutputStream;
		this.filename = filename;
		this.modificationTime = modificationTime;
		this.permissions = permissions;
		this.pool = pool;
		this.completeRunnable = completeRunnable;
		ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(new InterruptDelegate() {
			public void interrupt() {
				try {
					if (ftpClient.connected()) {
						ftpClient.quitImmediately();
					}
				} catch (Exception ignore) {
					ignore.getCause();
				}
			}
		});
	}

	private void safeQuit(boolean failed) {
		try {
			if (ftpClient.connected()) {
				if (failed && filename != null) {
					ftpClient.delete(ftpOutputStream.getRemoteFile());
				}
			}
		} catch (Exception ignore) {
			ignore.getCause();
		} finally {
			try {
				ftpOutputStream.close();
			} catch (IOException ignore) {
				ignore.getCause();
			}
			pool.checkIn(ftpClient);
			if (completeRunnable != null) {
				completeRunnable.run();
				completeRunnable = null;
			}
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
		}
	}

	/*
	 * (non-Javadoc)
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

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			ftpOutputStream.close();
			try {
				String actualFilename = (filename != null) ? filename : ftpOutputStream.getRemoteFile();
				if (filename != null) {
					if (ftpClient.exists(filename)) {
						ftpClient.delete(filename);
						filename = null;
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
				throw new IOException(e.getMessage()); // $codepro.audit.disable exceptionUsage.exceptionCreation
			}
		} finally {
			safeQuit(false);
		}
	}

	/*
	 * (non-Javadoc)
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
