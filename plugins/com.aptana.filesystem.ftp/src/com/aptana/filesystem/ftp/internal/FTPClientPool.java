package com.aptana.filesystem.ftp.internal;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.pro.ProFTPClient;

public class FTPClientPool extends ObjectPool<FTPClient>
{

	private FTPConnectionFileManager manager;

	public FTPClientPool(FTPConnectionFileManager manager)
	{
		super();
		this.manager = manager;
	}

	@Override
	protected FTPClient create()
	{
		try
		{
			FTPClient client = new ProFTPClient();
			manager.initAndAuthFTPClient(client, new NullProgressMonitor());
			return client;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void expire(FTPClient ftpClient)
	{
		try
		{
			ftpClient.quit();
		}
		catch (Exception e)
		{
			try
			{
				ftpClient.quitImmediately();
			}
			catch (Exception ignore)
			{
			}
			// throw new CoreException(new Status(Status.ERROR, FTPPlugin.PLUGIN_ID,
			// Messages.FTPConnectionFileManager_disconnect_failed, e));
		}
	}

	@Override
	public boolean validate(FTPClient o)
	{
		if (!o.connected())
			return false;
		try
		{
			o.noOperation();
		}
		catch (Exception e)
		{
			// ignore
			return false;
		}
		return true;
	}
}