package com.aptana.filesystem.ftp.tests;

import com.aptana.filesystem.ftp.internal.FTPConnectionFileManager;
import com.enterprisedt.net.ftp.FTPClient;

public class FTPProxiedConnectionFileManager extends FTPConnectionFileManager
{

	@Override
	protected FTPClient createFTPClient()
	{
		return new FTPClientProxy();
	}

}
