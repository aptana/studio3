package com.aptana.filesystem.ftp.wrappers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.filesystem.ftp.internal.FTPConnectionFileManager;
import com.aptana.ide.core.io.ConnectionContext;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;

public class FTPProxiedConnectionFileManager extends FTPConnectionFileManager
{
	// Exceptions we could set/force on the client
	public void setFTPException(boolean value)
	{
		((FTPClientProxy) ftpClient).setFTPException(value);
	}

	public boolean getFTPException()
	{
		return ((FTPClientProxy) ftpClient).getFTPException();
	}

	public void setIOException(boolean value)
	{
		((FTPClientProxy) ftpClient).setIOException(value);
	}

	public boolean getIOException()
	{
		return ((FTPClientProxy) ftpClient).getIOException();
	}

	public void setOperationCanceledException(boolean value)
	{
		((FTPClientProxy) ftpClient).setOperationCanceledException(value);
	}

	public boolean getOperationCanceledException()
	{
		return ((FTPClientProxy) ftpClient).getOperationCanceledException();
	}

	public void setCoreException(boolean value)
	{
		((FTPClientProxy) ftpClient).setCoreException(value);
	}

	public boolean getCoreException()
	{
		return ((FTPClientProxy) ftpClient).getCoreException();
	}

	public void setUnknownHostException(boolean value)
	{
		((FTPClientProxy) ftpClient).setUnknownHostException(value);
	}

	public boolean getUnknownHostException()
	{
		return ((FTPClientProxy) ftpClient).getUnknownHostException();
	}

	public void setFileNotFoundException(boolean value)
	{
		((FTPClientProxy) ftpClient).setFileNotFoundException(value);
	}

	public boolean getFileNotFoundException()
	{
		return ((FTPClientProxy) ftpClient).getFileNotFoundException();
	}

	public void forceStreamException(boolean value)
	{
		((FTPClientProxy) ftpClient).forceStreamException(value);
	}

	public boolean getStreamException()
	{
		return ((FTPClientProxy) ftpClient).getStreamException();
	}

	@Override
	protected FTPClient createFTPClient()
	{
		return new FTPClientProxy();
	}

	/**
	 * Public wrapper for protected method.
	 * 
	 * @param context
	 * @param monitor
	 * @param rootPath
	 * @param testPath
	 * @return
	 * @throws FTPException
	 * @throws IOException
	 * @throws ParseException
	 */
	public long determineServerTimeZoneShift(ConnectionContext context, IProgressMonitor monitor, IPath rootPath,
			IPath testPath) throws FTPException, IOException, ParseException
	{
		return super.determineServerTimeZoneShift(context, monitor, rootPath, testPath);
	}

	@Override
	public synchronized InputStream openInputStream(IPath path, int options, IProgressMonitor monitor)
			throws CoreException
	{
		if (getStreamException())
		{
			IStatus s = new Status(IStatus.ERROR, "com.aptana.ide.ftp.tests", "Forced error");
			throw new CoreException(s);
		}

		return super.openInputStream(path, options, monitor);
	}

	@Override
	public synchronized OutputStream openOutputStream(IPath path, int options, IProgressMonitor monitor)
			throws CoreException
	{
		if (getStreamException())
		{
			IStatus s = new Status(IStatus.ERROR, "com.aptana.ide.ftp.tests", "Forced error");
			throw new CoreException(s);
		}

		return super.openOutputStream(path, options, monitor);
	}
}
