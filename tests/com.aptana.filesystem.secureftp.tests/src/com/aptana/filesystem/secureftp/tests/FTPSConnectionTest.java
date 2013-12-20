/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.filesystem.secureftp.tests;

import org.eclipse.core.runtime.Path;

import com.aptana.core.io.tests.CommonConnectionTest;
import com.aptana.filesystem.secureftp.FTPSConnectionPoint;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("nls")
public class FTPSConnectionTest extends CommonConnectionTest
{

	@Override
	public void setUp() throws Exception
	{
		FTPSConnectionPoint ftpcp = new FTPSConnectionPoint();
		ftpcp.setHost(getConfig().getProperty("ftps.host")); //$NON-NLS-1$
		ftpcp.setLogin(getConfig().getProperty("ftps.username")); //$NON-NLS-1$
		ftpcp.setPassword(getConfig().getProperty("ftps.password").toCharArray());
		ftpcp.setPath(new Path(getConfig().getProperty("ftps.path")));
		ftpcp.setValidateCertificate(false);
		cp = ftpcp;
		super.setUp();
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
