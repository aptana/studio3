/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.core.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.old.ILogger;
import com.aptana.ide.syncing.core.old.Synchronizer;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;

/**
 * @author Kevin Lindsey
 */
@SuppressWarnings("nls")
public abstract class SyncingErrorTests
{
	protected IFileStore clientDirectory;
	protected IFileStore serverDirectory;

	protected IConnectionPoint clientManager;
	protected IConnectionPoint serverManager;

	protected static final Properties getConfig()
	{
		return SyncingTests.getConfig();
	}

	@Before
	public void setUp() throws Exception
	{
		ConnectionContext context = new ConnectionContext();
		context.put(ConnectionContext.COMMAND_LOG, System.out);
		CoreIOPlugin.setConnectionContext(clientManager, context);

		context.put(ConnectionContext.COMMAND_LOG, System.out);
		CoreIOPlugin.setConnectionContext(serverManager, context);

		clientDirectory = clientManager.getRoot().getFileStore(new Path("/client" + System.currentTimeMillis()));
		assertNotNull(clientDirectory);
		clientDirectory.mkdir(EFS.NONE, null);

		serverDirectory = serverManager.getRoot().getFileStore(new Path("/server" + System.currentTimeMillis()));
		assertNotNull(serverDirectory);
		serverDirectory.mkdir(EFS.NONE, null);
	}

	@After
	public void tearDown() throws Exception
	{
		try
		{
			if (clientDirectory.fetchInfo().exists())
			{
				// clientDirectory.delete(EFS.NONE, null);
				// assertFalse(clientDirectory.fetchInfo().exists());
			}
		}
		finally
		{
			if (clientManager.isConnected())
			{
				clientManager.disconnect(null);
			}
		}

		try
		{
			if (serverDirectory.fetchInfo().exists())
			{
				// serverDirectory.delete(EFS.NONE, null);
				// assertFalse(serverDirectory.fetchInfo().exists());
			}
		}
		finally
		{
			if (serverManager.isConnected())
			{
				serverManager.disconnect(null);
			}
		}
	}

	/*
	 * Sync Item Tests
	 */
	@Test
	public void testCancelMonitorDuringSync() throws IOException, CoreException
	{
		syncTest(false, System.currentTimeMillis());
	}

	/**
	 * Tests synchronization cancelling to see what happens to the FTP server.
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public void syncTest(boolean includeCloakedFiles, long sysTime) throws IOException, CoreException
	{
		String CLIENT_TEST = "client_test";
		String SERVER_TEST = "server_test";
		int timeTolerance = 150000;

		IFileStore clientTestDirectory = clientDirectory.getFileStore(new Path("/" + CLIENT_TEST + sysTime));
		IFileStore serverTestDirectory = serverDirectory.getFileStore(new Path("/" + SERVER_TEST + sysTime));
		serverTestDirectory.mkdir(EFS.NONE, null);

		// clone version x of github-services to local directory (newer)
		System.out.println("1) Writing github repo to " + EFSUtils.getAbsolutePath(clientTestDirectory));
		runGitClone("http://github.com/DmitryBaranovskiy/raphael.git", clientDirectory, clientTestDirectory.getName());

		Synchronizer syncManager = new Synchronizer(true, timeTolerance, includeCloakedFiles);
		syncManager.setLogger(new ILogger()
		{

			public void logWarning(String message, Throwable th)
			{
				System.out.println(message);
			}

			public void logInfo(String message, Throwable th)
			{
				System.out.println(message);
			}

			public void logError(String message, Throwable th)
			{
				System.out.println(message);
			}
		});

		System.out.println("2) upload from server_local to server_test");

		IProgressMonitor monitor = new NullProgressMonitor()
		{

			private int work_total = 0;

			@Override
			public void worked(int work)
			{
				work_total += work;
				System.out.println("worked " + work_total);
				if (work_total >= 2)
				{
					this.setCanceled(true);
				}
			}
		};

		VirtualFileSyncPair[] items = null;
		try
		{
			items = syncManager.getSyncItems(clientManager, serverManager, clientTestDirectory, serverTestDirectory,
					monitor);
			fail();
		}
		catch (OperationCanceledException ex)
		{
			assertNull(items);
		}

		items = syncManager.getSyncItems(clientManager, serverManager, clientTestDirectory, serverTestDirectory, null);

		IProgressMonitor monitor2 = new NullProgressMonitor()
		{

			private int work_total = 0;

			@Override
			public void worked(int work)
			{
				work_total += work;
				System.out.println("worked " + work_total);
				if (work_total >= 2)
				{
					this.setCanceled(true);
				}
			}
		};

		try
		{
			syncManager.upload(items, monitor2);
			fail();
		}
		catch (OperationCanceledException ex)
		{
			assertTrue(syncManager.getClientFileTransferedCount() < items.length);
		}

	}

	protected void runGitClone(String url, IFileStore basePath, String directory)
	{
		IStatus results = GitExecutable.instance().runInBackground(new Path(EFSUtils.getAbsolutePath(basePath)),
				"clone", url, directory);
		if (results == null || !results.isOK())
		{
			fail("Git clone failed");
		}
	}

}
