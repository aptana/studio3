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
import org.junit.experimental.categories.Category;

import com.aptana.filesystem.ftp.FTPConnectionPoint;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.testing.categories.IntegrationTests;

@Category({ IntegrationTests.class })
public class FTPLargeSampleSyncingTest extends LargeSampleSyncingTests
{

	@Override
	public void setUp() throws Exception
	{
		File baseTempFile = File.createTempFile("test", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
		baseTempFile.deleteOnExit();

		File baseDirectory = baseTempFile.getParentFile();

		LocalConnectionPoint lcp = new LocalConnectionPoint();
		lcp.setPath(new Path(baseDirectory.getAbsolutePath()));
		clientManager = lcp;

		FTPConnectionPoint ftpcp = new FTPConnectionPoint();
		ftpcp.setHost(getConfig().getProperty("ftp.host")); //$NON-NLS-1$
		ftpcp.setLogin(getConfig().getProperty("ftp.username")); //$NON-NLS-1$
		ftpcp.setPassword(getConfig().getProperty("ftp.password").toCharArray()); //$NON-NLS-1$
		ftpcp.setPath(new Path(getConfig().getProperty("ftp.path"))); //$NON-NLS-1$
		serverManager = ftpcp;

		super.setUp();
	}

}
