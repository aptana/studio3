/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeInFinally

package com.aptana.filesystem.secureftp.internal;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.Status;

import com.enterprisedt.net.ftp.FileTransferInputStream;

/**
 * @author Max Stepanov
 *
 */
public class SFTPFileDownloadInputStream extends InputStream {

	private FileTransferInputStream ftpInputStream;
	
	/**
	 * 
	 */
	public SFTPFileDownloadInputStream(FileTransferInputStream ftpInputStream) {
		this.ftpInputStream = ftpInputStream;
	}
	
	private void safeClose() {
		try {
			ftpInputStream.close();
		} catch (IOException e) {
			SecureFTPPlugin.log(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, Messages.SFTPFileDownloadInputStream_ErrorDownload, e));
		}
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		try {
			return ftpInputStream.read();
		} catch (IOException e) {
			safeClose();
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
			safeClose();
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
			safeClose();
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
			safeClose();
			throw e;
		}
	}

}
