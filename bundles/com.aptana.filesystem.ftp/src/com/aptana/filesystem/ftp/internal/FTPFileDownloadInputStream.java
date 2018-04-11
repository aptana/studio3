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
import java.io.InputStream;

import com.aptana.core.util.ProgressMonitorInterrupter;
import com.aptana.core.util.ProgressMonitorInterrupter.InterruptDelegate;
import com.enterprisedt.net.ftp.FTPClientInterface;
import com.enterprisedt.net.ftp.FileTransferInputStream;

/**
 * @author Max Stepanov
 */
public class FTPFileDownloadInputStream extends InputStream {

	private FTPClientInterface ftpClient;
	private FileTransferInputStream ftpInputStream;
	private FTPClientPool pool;

	/**
	 * @param pool
	 */
	public FTPFileDownloadInputStream(FTPClientPool pool, FTPClientInterface _ftpClient, FileTransferInputStream ftpInputStream) {
		this.ftpClient = _ftpClient;
		this.pool = pool;
		this.ftpInputStream = ftpInputStream;
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

	private void safeQuit() {
		try {
			ftpInputStream.close();
		} catch (IOException ignore) {
			ignore.getCause();
		}
		pool.checkIn(ftpClient);
		ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
	}

	/*
	 * (non-Javadoc)
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

	/*
	 * (non-Javadoc)
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

	/*
	 * (non-Javadoc)
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

	/*
	 * (non-Javadoc)
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
