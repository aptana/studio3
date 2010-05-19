package com.aptana.filesystem.ftp.internal;

import com.aptana.core.util.ReapingObjectPool;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPClientInterface;

public class FTPClientPool extends ReapingObjectPool<FTPClientInterface>
{

	private BaseFTPConnectionFileManager manager;

	public FTPClientPool(BaseFTPConnectionFileManager manager)
	{
		super(15 * 60 * 1000); // 15 minutes
		this.manager = manager;
	}

	@Override
	public FTPClientInterface create()
	{
		return manager.newClient();
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