/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.core.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;

import com.aptana.core.ILogger;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.syncing.core.old.Synchronizer;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;

/**
 * @author Kevin Lindsey
 */
@SuppressWarnings("nls")
public abstract class SyncingErrorTests extends TestCase
{
	protected IFileStore clientDirectory;
	protected IFileStore serverDirectory;

	protected IConnectionPoint clientManager;
	protected IConnectionPoint serverManager;
	
	private static Properties cachedProperties;

	protected static final Properties getConfig() {
		if (cachedProperties == null) {
			cachedProperties = new Properties();
			String propertiesFile = System.getenv("junit.properties");
			if (propertiesFile != null && new File(propertiesFile).length() > 0) {
				try {
					cachedProperties.load(new FileInputStream(propertiesFile));
				} catch (IOException ignore) {
				}
			}
		}
		return cachedProperties;
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
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

		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		try
		{
			if (clientDirectory.fetchInfo().exists())
			{
				//clientDirectory.delete(EFS.NONE, null);
				//assertFalse(clientDirectory.fetchInfo().exists());
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
				//serverDirectory.delete(EFS.NONE, null);
				//assertFalse(serverDirectory.fetchInfo().exists());
			}
		}
		finally
		{
			if (serverManager.isConnected())
			{
				serverManager.disconnect(null);
			}
		}

		super.tearDown();
	}

	/*
	 * Sync Item Tests
	 */
	public void testCancelMonitorDuringSync() throws IOException, CoreException
	{
		syncTest(false, System.currentTimeMillis());
	}

	/**
	 * Tests synchronization cancelling to see what happens to the FTP server.
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
		System.out.println("1) Writing github repo to " + EFSUtils.getAbsolutePath(clientTestDirectory) );
		runGitClone("http://github.com/DmitryBaranovskiy/raphael.git", clientDirectory, clientTestDirectory.getName());
		
		Synchronizer syncManager = new Synchronizer(true, timeTolerance, includeCloakedFiles);
		syncManager.setLogger(new ILogger() {

			public void logWarning(String message, Throwable th) {
				System.out.println(message);
			}

			public void logInfo(String message, Throwable th) {
				System.out.println(message);
			}

			public void logError(String message, Throwable th) {
				System.out.println(message);
			}
		});
		
		System.out.println("2) upload from server_local to server_test");
		
		IProgressMonitor monitor = new NullProgressMonitor() {
			
			private int work_total = 0;
			
			@Override
			public void worked(int work) {
				work_total += work;
				System.out.println("worked " + work_total);
				if(work_total >= 2) {
					this.setCanceled(true);
				}
			}
		};

		VirtualFileSyncPair[] items = null;
		try {
			items = syncManager.getSyncItems(clientManager, serverManager, clientTestDirectory,
					serverTestDirectory, monitor);			
			fail();
		}
		catch (OperationCanceledException ex) {
			assertNull(items);
		}

		items = syncManager.getSyncItems(clientManager, serverManager, clientTestDirectory,
				serverTestDirectory, null);

		IProgressMonitor monitor2 = new NullProgressMonitor() {
			
			private int work_total = 0;
			
			@Override
			public void worked(int work) {
				work_total += work;
				System.out.println("worked " + work_total);
				if(work_total >= 2) {
					this.setCanceled(true);
				}
			}
		};

		try {
			syncManager.upload(items, monitor2);
			fail();
		}
		catch (OperationCanceledException ex) {
			assertTrue(syncManager.getClientFileTransferedCount() < items.length);
		}
	
	}

	protected void runGitClone(String url, IFileStore basePath, String directory) {
		Map<Integer, String> results = GitExecutable.instance().runInBackground(new Path(EFSUtils.getAbsolutePath(basePath)), "clone", url, directory);
		if (results != null && results.keySet().iterator().next() != 0)
		{
			fail("Git clone failed");
		}
	}

}
