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
import com.aptana.filesystem.secureftp.SFTPConnectionPoint;

/**
 * @author Max Stepanov
 */
public class SFTPConnectionTest extends CommonConnectionTest
{

	@Override
	protected void setUp() throws Exception
	{
		SFTPConnectionPoint ftpcp = new SFTPConnectionPoint();
		ftpcp.setHost(getConfig().getProperty("sftp.host", "10.0.1.30")); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setLogin(getConfig().getProperty("sftp.username", "ftpuser")); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setPassword(getConfig().getProperty("sftp.password",	//$NON-NLS-1$
				String.valueOf(new char[] { 'l', 'e', 't', 'm', 'e', 'i', 'n'})).toCharArray());
		ftpcp.setPort(Integer.valueOf(getConfig().getProperty("sftp.port", "22"))); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setPath(Path.fromPortableString(getConfig().getProperty("sftp.path","/home/ftpuser"))); //$NON-NLS-1$ //$NON-NLS-2$
		cp = ftpcp;
		super.setUp();
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
