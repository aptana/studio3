/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeInFinally
// $codepro.audit.disable exceptionUsage.exceptionCreation

package com.aptana.filesystem.secureftp.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.eclipse.core.runtime.Status;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferOutputStream;
import com.enterprisedt.net.ftp.ssh.SSHFTPClient;
import com.enterprisedt.net.j2ssh.sftp.SshFxpStatus;

/**
 * @author Max Stepanov
 *
 */
public class SFTPFileUploadOutputStream extends OutputStream {

	private SSHFTPClient ftpClient;
	private FileTransferOutputStream ftpOutputStream;
	private String filename;
	private Date modificationTime;
	private long permissions;
	private Runnable completeRunnable;
	
	/**
	 * 
	 */
	public SFTPFileUploadOutputStream(SSHFTPClient ftpClient, FileTransferOutputStream ftpOutputStream, String filename, Date modificationTime, long permissions, Runnable completeRunnable) {
		this.ftpClient = ftpClient;
		this.ftpOutputStream = ftpOutputStream;
		this.filename = filename;
		this.modificationTime = modificationTime;
		this.permissions = permissions;
		this.completeRunnable = completeRunnable;
	}

	private void safeClose(boolean failed) {
		try {
			if (ftpClient.connected()) {
				if (failed && filename != null) {
					ftpClient.delete(ftpOutputStream.getRemoteFile());
				}
			}
		} catch (Exception e) {
			SecureFTPPlugin.log(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, Messages.SFTPFileUploadOutputStream_ErrorUpload, e));
		} finally {
			try {
				ftpOutputStream.close();
			} catch (IOException e) {
				SecureFTPPlugin.log(new Status(Status.ERROR, SecureFTPPlugin.PLUGIN_ID, Messages.SFTPFileUploadOutputStream_ErrorCloseStream, e));
			}
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
			safeClose(true);
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
				String actualFilename = (filename != null) ? filename : ftpOutputStream.getRemoteFile();
				if (filename != null) {
					if (ftpClient.exists(filename)) {
						ftpClient.delete(filename);
						filename = null;
					}
					ftpClient.rename(ftpOutputStream.getRemoteFile(), actualFilename);
					filename = null;
				}
				try {
					if (modificationTime != null) {
						ftpClient.setModTime(actualFilename, modificationTime);
					}
					if (permissions > 0) {
						ftpClient.changeMode((int) (permissions & 0777), actualFilename);
					}
				} catch (FTPException e) {
					if (e.getReplyCode() != SshFxpStatus.STATUS_FX_PERMISSION_DENIED) {
						throw e;
					}
				}
			} catch (FTPException e) {
				safeClose(true);
				IOException io = new IOException();
				io.initCause(e);
				throw io; 
			}
		} finally {
			safeClose(false);
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
			safeClose(true);
			throw e;
		}		
	}

}
