package com.aptana.filesystem.ftp.internal;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.util.ObjectPool;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPClientInterface;

public class FTPClientPool extends ObjectPool<FTPClientInterface>
{

	private BaseFTPConnectionFileManager manager;

	public FTPClientPool(BaseFTPConnectionFileManager manager)
	{
		super(15 * 60 * 1000); // 15 minutes
		this.manager = manager;
	}

	@Override
	protected FTPClientInterface create()
	{
		FTPClientInterface client = null;
		try
		{
			client = manager.newClient();
			manager.initAndAuthFTPClient(client, new NullProgressMonitor());
			return client;
		}
		catch (Exception e)
		{
			// TODO retry? log?
			e.printStackTrace();
			expire(client);
		}
		// FIXME We can't ever return null here, it messes things up! Need to retry?
		return null;
	}

	@Override
	public void expire(FTPClientInterface ftpClient)
	{
		if (ftpClient == null)
			return;
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
		}
	}

	@Override
	public boolean validate(FTPClientInterface o)
	{
		if (!o.connected())
			return false;
		if (o instanceof FTPClient)
		{
			try
			{
				((FTPClient) o).noOperation();
			}
			catch (Exception e)
			{
				// ignore
				return false;
			}
		}
		return true;
	}

}