/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.secureftp.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.experimental.categories.Category;

import com.aptana.core.io.tests.CommonConnectionTest;
import com.aptana.filesystem.secureftp.FTPSConnectionPoint;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.testing.categories.IntegrationTests;

/**
 * @author Max Stepanov
 */
@Category({ IntegrationTests.class })
public class FTPSConnectionWithBasePathTest extends CommonConnectionTest
{

	private static FTPSConnectionPoint setupConnection()
	{
		FTPSConnectionPoint ftpcp = new FTPSConnectionPoint();
		ftpcp.setHost(getConfig().getProperty("ftps.host")); //$NON-NLS-1$
		ftpcp.setLogin(getConfig().getProperty("ftps.username")); //$NON-NLS-1$
		ftpcp.setPassword(getConfig().getProperty("ftps.password").toCharArray());
		ftpcp.setValidateCertificate(false);

		ConnectionContext context = new ConnectionContext();
		context.put(ConnectionContext.COMMAND_LOG, System.out);
		CoreIOPlugin.setConnectionContext(ftpcp, context);

		return ftpcp;
	}

	@Override
	public void setUp() throws Exception
	{
		initBasePath();
		FTPSConnectionPoint ftpcp = setupConnection();
		ftpcp.setPath(constructBasePath());
		cp = ftpcp;
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		cleanupBasePath();
	}

	public static IPath constructBasePath()
	{
		return new Path(getConfig().getProperty("ftps.path")).append(FTPSConnectionWithBasePathTest.class
				.getSimpleName());
	}

	public static void initBasePath() throws CoreException
	{
		FTPSConnectionPoint ftpcp = setupConnection();
		IFileStore fs = ftpcp.getRoot().getFileStore(constructBasePath());
		assertNotNull(fs);
		try
		{
			if (!fs.fetchInfo().exists())
			{
				fs.mkdir(EFS.NONE, null);
			}
		}
		finally
		{
			ftpcp.disconnect(null);
		}
		assertFalse(ftpcp.isConnected());
	}

	public static void cleanupBasePath() throws CoreException
	{
		FTPSConnectionPoint ftpcp = setupConnection();
		IFileStore fs = ftpcp.getRoot().getFileStore(constructBasePath());
		assertNotNull(fs);
		try
		{
			if (fs.fetchInfo().exists())
			{
				fs.delete(EFS.NONE, null);
			}
		}
		finally
		{
			ftpcp.disconnect(null);
		}
		assertFalse(ftpcp.isConnected());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.tests.CommonConnectionTest#supportsSetModificationTime()
	 */
	@Override
	protected boolean supportsSetModificationTime()
	{
		return Boolean.parseBoolean(getConfig().getProperty("ftps.supports.setmodtime"));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.tests.CommonConnectionTest#supportsFolderSetModificationTime()
	 */
	@Override
	protected boolean supportsFolderSetModificationTime()
	{
		return Boolean.parseBoolean(getConfig().getProperty("ftps.supports.foldersetmodtime"));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.tests.CommonConnectionTest#supportsChangeGroup()
	 */
	@Override
	protected boolean supportsChangeGroup()
	{
		return Boolean.parseBoolean(getConfig().getProperty("ftps.supports.changegroup"));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.tests.CommonConnectionTest#supportsChangePermissions()
	 */
	@Override
	protected boolean supportsChangePermissions()
	{
		return Boolean.parseBoolean(getConfig().getProperty("ftps.supports.permissions"));
	}
}
