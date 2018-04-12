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

import com.aptana.filesystem.secureftp.SFTPConnectionPoint;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.LocalConnectionPoint;
import com.aptana.testing.categories.IntegrationTests;

@Category({ IntegrationTests.class })
public class SFTPSyncingTest extends SyncingTests
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

		SFTPConnectionPoint ftpcp = new SFTPConnectionPoint();
		ftpcp.setHost(getConfig().getProperty("sftp.host")); //$NON-NLS-1$
		ftpcp.setLogin(getConfig().getProperty("sftp.username")); //$NON-NLS-1$
		ftpcp.setPassword(getConfig().getProperty("sftp.password").toCharArray()); //$NON-NLS-1$
		ftpcp.setPort(Integer.valueOf(getConfig().getProperty("sftp.port", "22"))); //$NON-NLS-1$ //$NON-NLS-2$
		ftpcp.setPath(Path.fromPortableString(getConfig().getProperty("sftp.path"))); //$NON-NLS-1$
		serverManager = ftpcp;

		ConnectionContext context = new ConnectionContext();
		context.put(ConnectionContext.COMMAND_LOG, System.out);
		CoreIOPlugin.setConnectionContext(ftpcp, context);

		super.setUp();
	}

}
