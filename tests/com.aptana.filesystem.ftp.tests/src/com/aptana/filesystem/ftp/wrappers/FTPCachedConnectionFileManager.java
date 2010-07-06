package com.aptana.filesystem.ftp.wrappers;

import com.aptana.filesystem.ftp.internal.FTPConnectionFileManager;

public class FTPCachedConnectionFileManager extends FTPConnectionFileManager
{

	public FTPCachedConnectionFileManager()
	{
		super();
		setCaching(true);
	}

}
