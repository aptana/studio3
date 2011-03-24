/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.ftp.internal;

import com.aptana.core.util.KeepAliveObjectPool;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPClientInterface;
import com.enterprisedt.net.ftp.FTPTransferType;

public final class FTPClientPool extends KeepAliveObjectPool<FTPClientInterface>
{

	private IPoolConnectionManager manager;

	public FTPClientPool(IPoolConnectionManager manager)
	{
		super(7 * 60 * 1000); // 7 minutes
		this.manager = manager;
		start();
	}

	public FTPClientInterface create()
	{
		return manager.newClient();
	}

	public void expire(FTPClientInterface ftpClient)
	{
		if (ftpClient == null)
		{
			return;
		}
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

	public boolean validate(FTPClientInterface o)
	{
		if (!o.connected())
		{
			return false;
		}
		if (o instanceof FTPClient)
		{
			try
			{
				((FTPClient) o).noOperation();
				((FTPClient) o).setType(FTPTransferType.BINARY);
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
