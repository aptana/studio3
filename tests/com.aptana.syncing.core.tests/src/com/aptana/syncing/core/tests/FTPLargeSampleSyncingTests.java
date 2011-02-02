/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.core.tests;

import java.io.File;

import org.eclipse.core.runtime.Path;

import com.aptana.filesystem.ftp.FTPConnectionPoint;
import com.aptana.ide.core.io.LocalConnectionPoint;

public class FTPLargeSampleSyncingTests extends LargeSampleSyncingTests
{

	@Override
	protected void setUp() throws Exception
	{
		File baseTempFile = File.createTempFile("test", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		baseTempFile.deleteOnExit();
		
		File baseDirectory = baseTempFile.getParentFile();
		
		LocalConnectionPoint lcp = new LocalConnectionPoint();
		lcp.setPath(new Path(baseDirectory.getAbsolutePath()));
		clientManager = lcp;

		FTPConnectionPoint ftpcp = new FTPConnectionPoint();
		ftpcp.setHost(getConfig().getProperty("ftp.host", "10.0.1.30")); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setLogin(getConfig().getProperty("ftp.username", "ftpuser")); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setPassword(getConfig().getProperty("ftp.password",	//$NON-NLS-1$
				String.valueOf(new char[] { 'l', 'e', 't', 'm', 'e', 'i', 'n'})).toCharArray());
		ftpcp.setPath(new Path(getConfig().getProperty("ftp.path", "/home/ftpuser"))); 
		serverManager = ftpcp;

		super.setUp();		
	}

	@Override
	protected void tearDown() throws Exception
	{
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
