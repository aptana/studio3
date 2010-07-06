package com.aptana.filesystem.ftp.wrappers;

import java.io.IOException;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.pro.ProFTPClient;

public class FTPClientProxy extends ProFTPClient
{
	protected boolean throwIOException;
	protected boolean throwFTPException; // Exception

	protected boolean throwOperationCanceledException; // RuntimeException
	protected boolean throwCoreException; // Exception
	protected boolean throwUnknownHostException; // IOException
	protected boolean throwFileNotFoundException; // IOException

	protected boolean forceStreamException;

	public void setIOException(boolean value)
	{
		throwIOException = value;
	}

	public boolean getIOException()
	{
		return throwIOException;
	}

	public void setFTPException(boolean value)
	{
		throwFTPException = value;
	}

	public boolean getFTPException()
	{
		return throwFTPException;
	}

	public void setOperationCanceledException(boolean value)
	{
		throwOperationCanceledException = value;
	}

	public boolean getOperationCanceledException()
	{
		return throwOperationCanceledException;
	}

	public void setCoreException(boolean value)
	{
		throwCoreException = value;
	}

	public boolean getCoreException()
	{
		return throwCoreException;
	}

	public void setUnknownHostException(boolean value)
	{
		throwUnknownHostException = value;
	}

	public boolean getUnknownHostException()
	{
		return throwUnknownHostException;
	}

	public void setFileNotFoundException(boolean value)
	{
		throwFileNotFoundException = value;
	}

	public boolean getFileNotFoundException()
	{
		return throwFileNotFoundException;
	}

	public void forceStreamException(boolean value)
	{
		forceStreamException = value;
	}

	public boolean getStreamException()
	{
		return forceStreamException;
	}

	@Override
	public void connect() throws IOException, FTPException
	{
		if (throwIOException)
		{
			throw new IOException();
		}

		if (throwFTPException)
		{
			throw new FTPException("Forced exception");
		}

		// TODO Auto-generated method stub
		super.connect();
	}

	@Override
	public void quit() throws IOException, FTPException
	{
		super.quit();
	}
}
