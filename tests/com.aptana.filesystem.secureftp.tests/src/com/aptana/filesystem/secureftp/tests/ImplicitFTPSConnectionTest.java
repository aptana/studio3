/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.secureftp.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.filesystem.secureftp.FTPSConnectionPoint;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;

/**
 * @author Max Stepanov
 */
public class ImplicitFTPSConnectionTest
{

	protected IConnectionPoint cp;

	@Before
	public void setUp() throws Exception
	{
		FTPSConnectionPoint ftpcp = new FTPSConnectionPoint();
		ftpcp.setHost("ftp.secureftp-test.com"); //$NON-NLS-1$
		ftpcp.setLogin("test"); //$NON-NLS-1$
		ftpcp.setPassword(new char[] { 't', 'e', 's', 't' });
		ftpcp.setExplicit(false);
		ftpcp.setValidateCertificate(false);
		cp = ftpcp;

		ConnectionContext context = new ConnectionContext();
		context.put(ConnectionContext.COMMAND_LOG, System.out);
		CoreIOPlugin.setConnectionContext(cp, context);
	}

	@After
	public void tearDown() throws Exception
	{
		if (cp.isConnected())
		{
			cp.disconnect(null);
		}
	}

	@Test
	public final void testConnect() throws CoreException
	{
		cp.connect(null);
		assertTrue(cp.isConnected());
		assertTrue(cp.canDisconnect());
		cp.disconnect(null);
		assertFalse(cp.isConnected());
		assertFalse(cp.canDisconnect());
	}

	@Test
	public final void testFetchRootInfo() throws CoreException
	{
		IFileStore fs = cp.getRoot();
		assertNotNull(fs);
		assertFalse(cp.isConnected());
		IFileInfo fi = fs.fetchInfo();
		assertTrue(cp.isConnected());
		assertNotNull(fi);
		assertTrue(fi.exists());
		assertTrue(fi.isDirectory());
		assertEquals(Path.ROOT.toPortableString(), fi.getName());
	}

}
