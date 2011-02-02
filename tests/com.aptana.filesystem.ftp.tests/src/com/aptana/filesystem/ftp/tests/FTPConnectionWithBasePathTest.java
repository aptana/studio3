/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.ftp.tests;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.io.tests.CommonConnectionTest;
import com.aptana.filesystem.ftp.FTPConnectionPoint;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("nls")
public class FTPConnectionWithBasePathTest extends CommonConnectionTest
{

	private static FTPConnectionPoint setupConnection()
	{
		FTPConnectionPoint ftpcp = new FTPConnectionPoint();
		ftpcp.setHost(getConfig().getProperty("ftp.host", "10.0.1.30")); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setLogin(getConfig().getProperty("ftp.username", "ftpuser")); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setPassword(getConfig().getProperty("ftp.password",	//$NON-NLS-1$
				String.valueOf(new char[] { 'l', 'e', 't', 'm', 'e', 'i', 'n'})).toCharArray());
		
		ConnectionContext context = new ConnectionContext();
		context.put(ConnectionContext.COMMAND_LOG, System.out);
		CoreIOPlugin.setConnectionContext(ftpcp, context);

		return ftpcp;
	}

	@Override
	protected void setUp() throws Exception
	{
		initBasePath();
		FTPConnectionPoint ftpcp = setupConnection();
		ftpcp.setPath(constructBasePath());
		cp = ftpcp;
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cleanupBasePath();
	}

	public static IPath constructBasePath() {
		return new Path(getConfig().getProperty("ftp.path", "/home/ftpuser")).append(FTPConnectionWithBasePathTest.class.getSimpleName());
	}
	
	public static void initBasePath() throws CoreException
	{
		FTPConnectionPoint ftpcp = setupConnection();
		IFileStore fs = ftpcp.getRoot().getFileStore(constructBasePath());
		assertNotNull(fs);
		try {
			if (!fs.fetchInfo().exists())
			{
				fs.mkdir(EFS.NONE, null);
			}
		} finally {
			ftpcp.disconnect(null);
		}
		assertFalse(ftpcp.isConnected());
	}

	public static void cleanupBasePath() throws CoreException
	{
		FTPConnectionPoint ftpcp = setupConnection();
		IFileStore fs = ftpcp.getRoot().getFileStore(constructBasePath());
		assertNotNull(fs);
		try {
			if (fs.fetchInfo().exists())
			{
				fs.delete(EFS.NONE, null);
			}
		} finally {
			ftpcp.disconnect(null);
		}
		assertFalse(ftpcp.isConnected());
	}

	/* (non-Javadoc)
	 * @see com.aptana.core.io.tests.CommonConnectionTest#supportsSetModificationTime()
	 */
	@Override
	protected boolean supportsSetModificationTime()
	{
		return true;
	}


	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.tests.CommonConnectionTest#supportsChangeGroup()
	 */
	@Override
	protected boolean supportsChangeGroup()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.tests.CommonConnectionTest#supportsChangePermissions()
	 */
	@Override
	protected boolean supportsChangePermissions()
	{
		return true;
	}
}
