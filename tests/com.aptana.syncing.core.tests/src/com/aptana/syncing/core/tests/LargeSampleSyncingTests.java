/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.old.ILogger;
import com.aptana.ide.syncing.core.old.Synchronizer;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;

/**
 * @author Ingo Muschenetz
 */
@SuppressWarnings({ "nls", "deprecation" })
public abstract class LargeSampleSyncingTests
{
	protected IFileStore clientDirectory;
	protected IFileStore serverDirectory;

	protected IConnectionPoint clientManager;
	protected IConnectionPoint serverManager;

	protected synchronized static final Properties getConfig()
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
	public void testSyncIgnoreCloakedFiles() throws IOException, CoreException
	{
		syncTest(false, System.currentTimeMillis());
	}

	/*
	 * Sync Item Tests. Commented out as we evidently can't upload a file that begins with '.' to FTP servers, at least
	 * not our local one.
	 */
	// public void testSyncIncludeCloakedFiles() throws IOException, CoreException
	// {
	// syncTest(true, System.currentTimeMillis());
	// }

	// @formatter:off
	/**
	 * Tests synchronization using a large sample size. Test does the following:
	 * 1) Checks out a git repo to a local directory (Directory A) at a particular tag (version 1.4)
	 * 2) Checkout out the same git repo to another local directory (Directory B) at an older tag (say version 1.3)
	 * 3) Copies the local git repo to a second directory (Directory C)
	 * 4) Uploads Directory B to the remote server (Directory D)
	 * 5) Compares A and C to make sure nothing changed during the upload
	 * 6) Compares C and D to make sure the upload succeeded
	 * 7) Does a Synchronization between A and D, deleting any orphaned files on D. This would simulate a user uploading a new website
	 * 8) Compare A & C again to make sure nothing changed on A during the sync.
	 * 9) Compare A & D to make sure they are identical
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	// @formatter:on
	public void syncTest(boolean includeCloakedFiles, long sysTime) throws IOException, CoreException
	{
		String CLIENT_TEST = "client_test";
		String CLIENT_CONTROL = "client_control";
		String SERVER_LOCAL = "server_local";
		String SERVER_TEST = "server_test";
		int timeTolerance = 60000;

		IFileStore clientTestDirectory = clientDirectory.getFileStore(new Path("/" + CLIENT_TEST + sysTime));
		IFileStore clientControlDirectory = clientDirectory.getFileStore(new Path("/" + CLIENT_CONTROL + sysTime));
		IFileStore serverLocalDirectory = clientDirectory.getFileStore(new Path("/" + SERVER_LOCAL + sysTime));
		IFileStore serverTestDirectory = serverDirectory.getFileStore(new Path("/" + SERVER_TEST + sysTime));
		serverTestDirectory.mkdir(EFS.NONE, null);

		// clone version x of github-services to local directory (newer)
		System.out.println("1) Writing github repo to " + EFSUtils.getAbsolutePath(clientTestDirectory));
		runGitClone("git://github.com/DmitryBaranovskiy/raphael.git", clientDirectory, clientTestDirectory.getName());

		System.out.println("2) Writing github repo to " + EFSUtils.getAbsolutePath(serverLocalDirectory));
		runGitClone("git://github.com/DmitryBaranovskiy/raphael.git", clientDirectory, serverLocalDirectory.getName());

		// checkout specific tags
		System.out.println("Checking out tag v1.4.0 on client_test");
		runGitTag(clientTestDirectory, "v1.4.0");

		System.out.println("Checking out tag v1.3.0 on server_local");
		runGitTag(serverLocalDirectory, "v1.3.0");

		System.out.println("3) Copying github repo to " + EFSUtils.getAbsolutePath(clientControlDirectory));
		clientTestDirectory.copy(clientControlDirectory, EFS.OVERWRITE, null);

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

		System.out.println("4) upload from server_local to server_test");
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, serverLocalDirectory,
				serverTestDirectory, null);
		syncManager.upload(items, null);

		System.out.println("5) ensure local version matches remote version");
		assertDirectoriesEqual(serverLocalDirectory, serverTestDirectory, includeCloakedFiles, timeTolerance);

		System.out
				.println("6) test to make sure client_test and client_control are identical, as nothing should have changed locally");
		assertDirectoriesEqual(clientTestDirectory, clientControlDirectory, includeCloakedFiles, timeTolerance);

		// Now get sync items between local and remote directories
		System.out.println("7) sync between client_test and server_test, deleting on remote");
		VirtualFileSyncPair[] items2 = syncManager.getSyncItems(clientManager, serverManager, clientTestDirectory,
				serverTestDirectory, null);
		syncManager.uploadAndDelete(items2, null);

		System.out
				.println("8) test to make sure client_test and client_control are identical, as nothing should have changed locally");
		assertDirectoriesEqual(clientTestDirectory, clientControlDirectory, includeCloakedFiles, timeTolerance);

		System.out.println("9) test to make sure client_test and server_test are identical after sync");
		assertDirectoriesEqual(clientTestDirectory, serverTestDirectory, includeCloakedFiles, timeTolerance);
	}

	protected void assertDirectoriesEqual(IFileStore root1, IFileStore root2, boolean includeCloakedFiles,
			int timeTolerance) throws CoreException
	{
		IFileStore[] ctd = EFSUtils.getFiles(root1, true, includeCloakedFiles);
		IFileStore[] ccd = EFSUtils.getFiles(root2, true, includeCloakedFiles);

		// create map of files on destination
		HashMap<String, IFileStore> map = new HashMap<String, IFileStore>();
		for (int i = 0; i < ccd.length; i++)
		{
			IFileStore fsTest = ccd[i];
			String fileRelPath = EFSUtils.getRelativePath(root2, fsTest, null);
			map.put(fileRelPath, fsTest);
		}

		assertEquals(ctd.length, ccd.length);

		// iterate through source and ensure all files made it to dest
		for (int i = 0; i < ctd.length; i++)
		{
			IFileStore fsControl = ctd[i];
			assertFilesExists(map, root1, fsControl, timeTolerance);
		}
	}

	protected void assertFilesEqual(IFileStore root1, IFileStore root2, IFileStore file1, IFileStore file2)
			throws CoreException
	{
		String file1RelPath = EFSUtils.getRelativePath(root1, file1, null);
		String file2RelPath = EFSUtils.getRelativePath(root2, file2, null);
		assertEquals(MessageFormat.format("File {0} and {1} not equal", file1RelPath, file2RelPath), file1RelPath,
				file2RelPath);
		IFileInfo f1 = file1.fetchInfo(IExtendedFileStore.DETAILED, null);
		IFileInfo f2 = file2.fetchInfo(IExtendedFileStore.DETAILED, null);
		if (!f1.isDirectory())
		{
			assertEquals(
					MessageFormat.format("File {0} and {1} modification times differ", file1RelPath, file2RelPath),
					f1.getLastModified(), f2.getLastModified());
			assertEquals(MessageFormat.format("File {0} and {1} different sizes", file1RelPath, file2RelPath),
					f1.getLength(), f2.getLength());
		}
	}

	protected void assertFilesExists(HashMap<String, IFileStore> destMap, IFileStore sourceRoot, IFileStore sourceFile,
			int timeTolerance) throws CoreException
	{
		String relPath = EFSUtils.getRelativePath(sourceRoot, sourceFile, null);
		IFileStore destFile = destMap.get(relPath);
		// System.out.println("Comparing " + relPath);

		assertNotNull(MessageFormat.format("File {0} not found on destination", relPath), destFile);
		IFileInfo f1 = sourceFile.fetchInfo(IExtendedFileStore.DETAILED, null);
		IFileInfo f2 = destFile.fetchInfo(IExtendedFileStore.DETAILED, null);
		if (!f1.isDirectory())
		{
			long sourceFileTime = f1.getLastModified();
			long destFileTime = f2.getLastModified();
			long timeDiff = destFileTime - sourceFileTime;

			assertTrue(MessageFormat.format("File {0} is {1} seconds newer on destination", relPath,
					(int) timeDiff / 1000), -timeTolerance <= timeDiff && timeDiff <= timeTolerance);
			assertEquals(MessageFormat.format("File {0} different sizes", relPath), f1.getLength(), f2.getLength());
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

	protected void runGitTag(IFileStore basePath, String tag)
	{
		IStatus results = GitExecutable.instance().runInBackground(new Path(EFSUtils.getAbsolutePath(basePath)),
				"checkout", "-b", tag);
		if (results == null || !results.isOK())
		{
			fail("Git tag failed");
		}
	}

}
