package com.aptana.filesystem.ftp.tests;

import com.aptana.filesystem.ftp.internal.FTPConnectionFileManager;
import com.enterprisedt.net.ftp.FTPClient;

public class FTPProxiedConnectionFileManager extends FTPConnectionFileManager
{
	public void setFTPException(boolean value) {
		((FTPClientProxy)ftpClient).setFTPException(value);
	}
	
	public boolean getFTPException() {
		return ((FTPClientProxy)ftpClient).getFTPException();
	}

	public void setIOException(boolean value) {
		((FTPClientProxy)ftpClient).setIOException(value);
	}

	public boolean getIOException() {
		return ((FTPClientProxy)ftpClient).getIOException();
	}

	public void setOperationCanceledException(boolean value) {
		((FTPClientProxy)ftpClient).setOperationCanceledException(value);
	}
	
	public boolean getOperationCanceledException() {
		return ((FTPClientProxy)ftpClient).getOperationCanceledException();
	}

	public void setCoreException(boolean value) {
		((FTPClientProxy)ftpClient).setCoreException(value);
	}
	
	public boolean getCoreException() {
		return ((FTPClientProxy)ftpClient).getCoreException();
	}

	public void setUnknownHostException(boolean value) {
		((FTPClientProxy)ftpClient).setUnknownHostException(value);
	}
	
	public boolean getUnknownHostException() {
		return ((FTPClientProxy)ftpClient).getUnknownHostException();
	}

	public void setFileNotFoundException(boolean value) {
		((FTPClientProxy)ftpClient).setFileNotFoundException(value);
	}
	
	public boolean getFileNotFoundException() {
		return ((FTPClientProxy)ftpClient).getFileNotFoundException();
	}
	
	@Override
	protected FTPClient createFTPClient()
	{
		return new FTPClientProxy();
	}

}
