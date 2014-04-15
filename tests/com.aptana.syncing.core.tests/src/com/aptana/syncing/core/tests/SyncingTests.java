/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.core.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.FileUtil;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.syncing.core.old.ILogger;
import com.aptana.ide.syncing.core.old.SyncState;
import com.aptana.ide.syncing.core.old.Synchronizer;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;

/**
 * @author Kevin Lindsey
 */
@SuppressWarnings("nls")
public abstract class SyncingTests
{
	protected IFileStore clientDirectory;
	protected IFileStore serverDirectory;

	protected IConnectionPoint clientManager;
	protected IConnectionPoint serverManager;

	protected String fileName = "test.txt";
	protected String folderName = "test";

	private static Properties cachedProperties;

	protected synchronized static final Properties getConfig()
	{
		if (cachedProperties == null)
		{
			cachedProperties = new Properties();
			String propertiesFile = System.getProperty("junit.properties");
			if (propertiesFile != null && new File(propertiesFile).length() > 0)
			{
				FileInputStream stream = null;
				try
				{
					stream = new FileInputStream(propertiesFile);
					cachedProperties.load(stream);
					if (IdeLog.isInfoEnabled(SyncingPlugin.getDefault(), null))
					{
						StringWriter strWriter = new StringWriter();
						cachedProperties.list(new PrintWriter(strWriter));
						IdeLog.logInfo(SyncingPlugin.getDefault(), "Loaded junit.properties: " + strWriter.toString());
					}
				}
				catch (IOException e)
				{
					IdeLog.logError(SyncingPlugin.getDefault(), "Failed to load junit.properties file at "
							+ propertiesFile, e);
				}
				finally
				{
					if (stream != null)
					{
						try
						{
							stream.close();
						}
						catch (IOException ignore)
						{
							// ignore
						}
					}
				}
			}
			else
			{
				IdeLog.logError(SyncingPlugin.getDefault(), "Expected, but did not find, testing properties at: "
						+ propertiesFile);
			}
		}
		return cachedProperties;
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp() throws Exception
	{
		ConnectionContext context = new ConnectionContext();
		context.put(ConnectionContext.COMMAND_LOG, System.out);
		CoreIOPlugin.setConnectionContext(clientManager, context);

		Random r = new Random();
		int i = r.nextInt();
		long millis = System.currentTimeMillis();

		clientDirectory = clientManager.getRoot().getFileStore(new Path("/client_" + millis + "_" + i));
		assertNotNull(clientDirectory);
		clientDirectory.mkdir(EFS.NONE, null);

		serverDirectory = serverManager.getRoot().getFileStore(new Path("/server_" + millis + "_" + i));
		assertNotNull(serverDirectory);
		serverDirectory.mkdir(EFS.NONE, null);

//		super.setUp();

	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	public void tearDown() throws Exception
	{
		try
		{
			if (clientDirectory.fetchInfo().exists())
			{
				clientDirectory.delete(EFS.NONE, null);
				assertFalse(clientDirectory.fetchInfo().exists());
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
				serverDirectory.delete(EFS.NONE, null);
				assertFalse(serverDirectory.fetchInfo().exists());
			}
		}
		finally
		{
			if (serverManager.isConnected())
			{
				serverManager.disconnect(null);
			}
		}

//		super.tearDown();
	}

	/**
	 * Recursively deletes a directory and its contents
	 * 
	 * @param file
	 */
	protected void removeDirectory(File file)
	{
		if (file.exists())
		{
			if (file.isDirectory())
			{
				File[] files = file.listFiles();

				for (int i = 0; i < files.length; i++)
				{
					File f = files[i];

					if (f.isDirectory())
					{
						removeDirectory(f);
					}
					else
					{
						f.delete();
					}
				}
			}

			file.delete();
		}
	}

	/**
	 * createServerDirectory
	 * 
	 * @param manager
	 * @param path
	 * @return IVirtualFile
	 * @throws CoreException
	 */
	protected IFileStore getDirectory(IFileStore basePath, String path) throws CoreException
	{
		return basePath.getFileStore(new Path(path));
	}

	/**
	 * createServerDirectory
	 * 
	 * @param manager
	 * @param path
	 * @return IVirtualFile
	 * @throws CoreException
	 */
	protected IFileStore getFile(IFileStore basePath, String path) throws CoreException
	{
		return basePath.getFileStore(new Path(path));
	}

	/**
	 * createClientDirectory
	 * 
	 * @param path
	 * @param modificationTime
	 * @return File
	 * @throws CoreException
	 */
	protected IFileStore createClientDirectory(String path, long modificationTime) throws CoreException
	{
		IFileStore fs = clientDirectory.getFileStore(new Path(path));
		fs.mkdir(0, null);

		IFileInfo fi = fs.fetchInfo();
		fi.setLastModified(modificationTime);
		fs.putInfo(fi, EFS.SET_LAST_MODIFIED, null);

		return fs;
	}

	/**
	 * createServerDirectory
	 * 
	 * @param path
	 * @param modificationTime
	 * @return File
	 * @throws CoreException
	 */
	protected IFileStore createServerDirectory(String path, long modificationTime) throws CoreException
	{
		IFileStore fs = serverDirectory.getFileStore(new Path(path));
		fs.mkdir(0, null);

		IFileInfo fi = fs.fetchInfo();
		fi.setLastModified(modificationTime);
		fs.putInfo(fi, EFS.SET_LAST_MODIFIED, null);

		return fs;
	}

	/**
	 * createDirectory
	 * 
	 * @param path
	 * @param modificationTime
	 * @return File
	 */
	protected File createDirectory(String path, long modificationTime)
	{
		File file = new File(path);
		file.mkdirs();
		file.setLastModified(modificationTime);

		return file;
	}

	/**
	 * createClientFile
	 * 
	 * @param path
	 * @param modificationTime
	 * @throws IOException
	 * @throws IOException
	 * @return File
	 * @throws CoreException
	 */
	protected IFileStore createClientFile(String path, long modificationTime) throws IOException, CoreException
	{
		return this.createClientFile(path, modificationTime, null);
	}

	/**
	 * createClientFile
	 * 
	 * @param path
	 * @param modificationTime
	 * @param content
	 * @throws IOException
	 * @return File
	 * @throws CoreException
	 */
	protected IFileStore createClientFile(String path, long modificationTime, String content) throws IOException,
			CoreException
	{
		return this.createFile(clientDirectory.getFileStore(new Path(path)), modificationTime, content);
	}

	/**
	 * createClientFile
	 * 
	 * @param path
	 * @param modificationTime
	 * @throws IOException
	 * @throws IOException
	 * @return File
	 * @throws CoreException
	 */
	protected IFileStore createServerFile(String path, long modificationTime) throws IOException, CoreException
	{
		return this.createServerFile(path, modificationTime, null);
	}

	/**
	 * createServerFile
	 * 
	 * @param path
	 * @param modificationTime
	 * @param content
	 * @throws IOException
	 * @return File
	 * @throws CoreException
	 */
	protected IFileStore createServerFile(String path, long modificationTime, String content) throws IOException,
			CoreException
	{
		return this.createFile(serverDirectory.getFileStore(new Path(path)), modificationTime, content);
	}

	/**
	 * createFile
	 * 
	 * @param path
	 * @param modificationTime
	 * @param content
	 * @throws IOException
	 * @return File
	 * @throws CoreException
	 */
	protected IFileStore createFile(IFileStore fs, long modificationTime, String content) throws IOException,
			CoreException
	{
		// System.out.print("Creating file: " + fs.toURI().toString() + ". ");

		if (fs.fetchInfo().isDirectory())
		{
			fs.mkdir(EFS.NONE, null);
		}
		else
		{
			fs.getParent().mkdir(EFS.NONE, null);
		}
		Writer w = new OutputStreamWriter(fs.openOutputStream(EFS.NONE, null));
		if (content != null)
		{
			w.write(content);
		}
		else
		{
			w.write("");
		}
		w.close();

		IFileInfo fi = fs.fetchInfo();

		// System.out.print("Created: " + fs.fetchInfo(IExtendedFileStore.DETAILED, null).getLastModified() + ". ");

		fi.setLastModified(modificationTime);
		fs.putInfo(fi, EFS.SET_LAST_MODIFIED, null);

		// System.out.print("Modified: " + fs.fetchInfo(IExtendedFileStore.DETAILED, null).getLastModified() + "\n");

		return fs;
	}

	/**
	 * getSyncItems
	 * 
	 * @return SyncItem[]
	 * @throws IOException
	 * @throws ConnectionException
	 */
	protected VirtualFileSyncPair[] getSyncItems() throws IOException, CoreException
	{
		return this.getSyncItems(false, 0);
	}

	/**
	 * getSyncItems
	 * 
	 * @param useCRC
	 * @param timeTolerance
	 * @return SyncItem[]
	 * @throws IOException
	 * @throws ConnectionException
	 * @throws CoreException
	 */
	protected VirtualFileSyncPair[] getSyncItems(boolean useCRC, int timeTolerance) throws IOException, CoreException
	{

		return this.getSyncItems(useCRC, timeTolerance, clientDirectory, serverDirectory);
	}

	/**
	 * getSyncItems
	 * 
	 * @param useCRC
	 * @param timeTolerance
	 * @return SyncItem[]
	 * @throws IOException
	 * @throws ConnectionException
	 * @throws CoreException
	 */
	protected VirtualFileSyncPair[] getSyncItems(boolean useCRC, int timeTolerance, IFileStore clientRoot,
			IFileStore serverRoot) throws IOException, CoreException
	{
		Synchronizer syncManager = new Synchronizer(useCRC, timeTolerance);

		syncManager.setLogger(new ILogger()
		{

			public void logWarning(String message, Throwable th)
			{
				// TODO Auto-generated method stub

			}

			public void logInfo(String message, Throwable th)
			{
				System.out.print(message);
			}

			public void logError(String message, Throwable th)
			{
				// TODO Auto-generated method stub

			}
		});

		return syncManager.getSyncItems(clientManager, serverManager, clientRoot, serverRoot, null);
	}

	/*
	 * Sync Item Tests
	 */

	/**
	 * testClientFileOnly
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientFileOnly() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime); //$NON-NLS-1$

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ClientItemOnly, items[0].getSyncState());
	}

	/**
	 * testClientDirectoryOnly
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientDirectoryOnly() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory(folderName, currentTime); //$NON-NLS-1$

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ClientItemOnly, items[0].getSyncState());
	}

	/**
	 * testClientFileIsNewer
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientFileIsNewer() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = fileName; //$NON-NLS-1$
		this.createClientFile(filename, currentTime);
		this.createServerFile(filename, currentTime - 10000);

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ClientItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testClientDirectoryIsNewer
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientDirectoryIsNewer() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String directoryName = folderName; //$NON-NLS-1$
		this.createClientDirectory(directoryName, currentTime);
		this.createServerDirectory(directoryName, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems();

		// we now delete remote directories
		assertSyncPairLength(0, items);
		// assertEquals(SyncState.ClientItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testServerFileOnly
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerFileOnly() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createServerFile(fileName, currentTime); //$NON-NLS-1$

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ServerItemOnly, items[0].getSyncState());
	}

	/**
	 * testServerDirectoryOnly
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerDirectoryOnly() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createServerDirectory(folderName, currentTime); //$NON-NLS-1$

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ServerItemOnly, items[0].getSyncState());
	}

	/**
	 * testServerFileIsNewer
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerFileIsNewer() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = fileName; //$NON-NLS-1$
		this.createClientFile(filename, currentTime - 1000);
		this.createServerFile(filename, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ServerItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testServerDirectoryIsNewer
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerDirectoryIsNewer() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String directoryName = folderName; //$NON-NLS-1$
		this.createClientDirectory(directoryName, currentTime - 1000);
		this.createServerDirectory(directoryName, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		// we now delete directories from sync
		assertSyncPairLength(0, items);
		// assertEquals(SyncState.ServerItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testFileTimesMatch
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileTimesMatch() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = fileName; //$NON-NLS-1$
		this.createClientFile(filename, currentTime);
		this.createServerFile(filename, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testDirectoryTimesMatch
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testDirectoryTimesMatch() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String directoryName = folderName; //$NON-NLS-1$
		this.createClientDirectory(directoryName, currentTime);
		this.createServerDirectory(directoryName, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		// we now delete directories
		assertSyncPairLength(0, items);
		// assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testFileTimesWithinTolerance1
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileTimesWithinTolerance1() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = fileName; //$NON-NLS-1$
		this.createClientFile(filename, currentTime - 1000);
		this.createServerFile(filename, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 1000);

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testFileTimesWithinTolerance2
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileTimesWithinTolerance2() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = fileName; //$NON-NLS-1$
		this.createClientFile(filename, currentTime);
		this.createServerFile(filename, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 1000);

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testFileTimesOutsideTolerance1
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileTimesOutsideTolerance1() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = fileName; //$NON-NLS-1$
		this.createClientFile(filename, currentTime - 1000);
		this.createServerFile(filename, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 999);

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ServerItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testFileTimesOutsideTolerance2
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileTimesOutsideTolerance2() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = fileName; //$NON-NLS-1$
		this.createClientFile(filename, currentTime);
		this.createServerFile(filename, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 999);

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ClientItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testDirectoryTimesWithinTolerance1
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testDirectoryTimesWithinTolerance1() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String dirname = folderName; //$NON-NLS-1$
		this.createClientDirectory(dirname, currentTime - 1000);
		this.createServerDirectory(dirname, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 1000);

		// we now delete directories
		assertSyncPairLength(0, items);
		// assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testDirectoryTimesWithinTolerance2
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testDirectoryTimesWithinTolerance2() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String dirname = folderName; //$NON-NLS-1$
		this.createClientDirectory(dirname, currentTime);
		this.createServerDirectory(dirname, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 1000);

		// we now delete directories
		assertSyncPairLength(0, items);
		// assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testDirectoryTimesOutsideTolerance1
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testDirectoryTimesOutsideTolerance1() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String dirname = folderName; //$NON-NLS-1$
		this.createClientDirectory(dirname, currentTime - 1000);
		this.createServerDirectory(dirname, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 999);

		// we now delete directories
		assertSyncPairLength(0, items);
		// assertEquals(SyncState.ServerItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testDirectoryTimesOutsideTolerance2
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testDirectoryTimesOutsideTolerance2() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String dirname = folderName; //$NON-NLS-1$
		this.createClientDirectory(dirname, currentTime);
		this.createServerDirectory(dirname, currentTime - 1000);

		VirtualFileSyncPair[] items = this.getSyncItems(false, 999);

		// we now delete directories
		assertSyncPairLength(0, items);
		// assertEquals(SyncState.ClientItemIsNewer, items[0].getSyncState());
	}

	/**
	 * testFilesCRCsDiffer()
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFilesCRCsDiffer() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = fileName; //$NON-NLS-1$
		String content = "abc123"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime, content);
		this.createServerFile(filename, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(true, 0);

		assertSyncPairLength(1, items);
		assertEquals(SyncState.CRCMismatch, items[0].getSyncState());
	}

	/**
	 * testFilesCRCsMatch()
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFilesCRCsMatch() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String filename = fileName; //$NON-NLS-1$
		String content = "abc123"; //$NON-NLS-1$
		this.createClientFile(filename, currentTime, content);
		this.createServerFile(filename, currentTime, content);

		VirtualFileSyncPair[] items = this.getSyncItems(true, 0);

		assertSyncPairLength(1, items);
		assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testDirectoryCRCsMatch This confirms that turning on CRC checking doesn't involve directories
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testDirectoryCRCsMatch() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String directoryName = folderName; //$NON-NLS-1$
		this.createClientDirectory(directoryName, currentTime);
		this.createServerDirectory(directoryName, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems(true, 0);

		// we now delete directories
		assertSyncPairLength(0, items);
		// assertEquals(SyncState.ItemsMatch, items[0].getSyncState());
	}

	/**
	 * testTypeMismatch1
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testTypeMismatch1() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String name = folderName; //$NON-NLS-1$
		this.createClientFile(name, currentTime);
		this.createServerDirectory(name, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertSyncPairLength(1, items);
		assertEquals(SyncState.IncompatibleFileTypes, items[0].getSyncState());
	}

	/**
	 * testTypeMismatch2
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testTypeMismatch2() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		String name = folderName; //$NON-NLS-1$
		this.createClientDirectory(name, currentTime);
		this.createServerFile(name, currentTime);

		VirtualFileSyncPair[] items = this.getSyncItems();

		assertSyncPairLength(1, items);
		assertEquals(SyncState.IncompatibleFileTypes, items[0].getSyncState());
	}

	/**
	 * testClientOnlyFileUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientOnlyFileUpload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		IFileStore clientFileOnServer = getFile(serverDirectory, clientDirectory.getName());
		assertFalse(
				"Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " exists.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$

		// sync
		syncManager.upload(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(1, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse(
				"Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " does not exist.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientOnlyDirectoryUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientOnlyDirectoryUpload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore newClientDirectory = this.createClientDirectory(folderName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);

		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		IFileStore clientFileOnServer = getDirectory(serverDirectory, newClientDirectory.getName());
		assertFalse(
				"Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " exists.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$

		// sync
		syncManager.upload(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(1, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " does not exist.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientNewerFileUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientNewerFileUpload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime); //$NON-NLS-1$
		this.createServerFile(fileName, currentTime - 1000); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.upload(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(1, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testClientNewerDirectoryUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientNewerDirectoryUpload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory(folderName, currentTime); //$NON-NLS-1$
		this.createServerDirectory(folderName, currentTime - 1000); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.upload(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsDifferUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileCRCsDifferUpload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile(fileName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.upload(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(1, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsMatchUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileCRCsMatchUpload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile(fileName, currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.upload(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testDirectoryCRCsMatchUpload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testDirectoryCRCsMatchUpload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory(folderName, currentTime); //$NON-NLS-1$
		this.createServerDirectory(folderName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.upload(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testServerOnlyFileUploadAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerOnlyFileUploadAndDelete() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore serverFile = this.createServerFile("delete.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.uploadAndDelete(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(1, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse(
				"Server file: " + EFSUtils.getAbsolutePath(serverFile) + " should be deleted.", serverFile.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * testServerOnlyDirectoryUploadAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerOnlyDirectoryUploadAndDelete() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore serverDir = this.createServerDirectory("delete", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.uploadAndDelete(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(1, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse(
				"Server file: " + EFSUtils.getAbsolutePath(serverDir) + " should be deleted.", serverDir.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerOnlyFileDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerOnlyFileDownload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore serverFile = this.createServerFile(fileName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.download(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());

		IFileStore serverFileOnClient = getFile(clientDirectory, serverFile.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(serverFileOnClient) + " does not exist.", serverFileOnClient.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerOnlyDirectoryDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerOnlyDirectoryDownload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore serverDir = this.createServerDirectory(folderName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.download(items, null);

		// check client counts
		assertEquals(1, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		IFileStore serverFileOnClient = getDirectory(clientDirectory, serverDir.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(serverFileOnClient) + " does not exist.", serverFileOnClient.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * testServerNewerFileDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerNewerFileDownload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime - 1000); //$NON-NLS-1$
		this.createServerFile(fileName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.download(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testServerNewerDirectoryDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerNewerDirectoryDownload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory(folderName, currentTime - 1000); //$NON-NLS-1$
		this.createServerDirectory(folderName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.download(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsDifferDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileCRCsDifferDownload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile(fileName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.download(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsMatchDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileCRCsMatchDownload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile(fileName, currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.download(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testDirectoryCRCsMatchDownload
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testDirectoryCRCsMatchDownload() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory(folderName, currentTime); //$NON-NLS-1$
		this.createServerDirectory(folderName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.download(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testClientOnlyFileDownloadAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientOnlyFileDownloadAndDelete() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore clientFile = this.createClientFile("delete.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.downloadAndDelete(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(1, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse(
				"Client file: " + EFSUtils.getAbsolutePath(clientFile) + " should be deleted.", clientFile.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientOnlyDirectoryDownloadAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientOnlyDirectoryDownloadAndDelete() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore clientFile = this.createClientDirectory("delete", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.downloadAndDelete(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(1, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse(
				"Client directory: " + EFSUtils.getAbsolutePath(clientFile) + " should be deleted.", clientFile.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientOnlyFileFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientOnlyFileFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore clientFile = this.createClientFile(fileName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(1, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		IFileStore clientFileOnServer = getFile(serverDirectory, clientFile.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " does not exist.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * testClientOnlyDirectoryFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientOnlyDirectoryFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore clientFile = this.createClientDirectory(folderName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(1, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		IFileStore clientFileOnServer = getDirectory(serverDirectory, clientFile.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(clientFileOnServer) + " does not exist.", clientFileOnServer.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientOnlyFileFullSyncAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientOnlyFileFullSyncAndDelete() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore clientFile = this.createClientFile("delete.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSyncAndDelete(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(1, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse(
				"Client file: " + EFSUtils.getAbsolutePath(clientFile) + " should be deleted.", clientFile.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientOnlyDirectoryFullSyncAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientOnlyDirectoryFullSyncAndDelete() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore clientFile = this.createClientDirectory("delete", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSyncAndDelete(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(1, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse(
				"Client directory: " + EFSUtils.getAbsolutePath(clientFile) + " should be deleted.", clientFile.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testClientNewerFileFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientNewerFileFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime); //$NON-NLS-1$
		this.createServerFile(fileName, currentTime - 1000); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(1, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testClientNewerDirectoryFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testClientNewerDirectoryFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory(folderName, currentTime); //$NON-NLS-1$
		this.createServerDirectory(folderName, currentTime - 1000); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testServerOnlyFileFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerOnlyFileFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore serverFile = this.createServerFile(fileName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());

		IFileStore serverFileOnClient = getFile(clientDirectory, serverFile.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(serverFile) + " does not exist.", serverFileOnClient.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerOnlyDirectoryFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerOnlyDirectoryFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore file = this.createServerDirectory(FileUtil.getRandomFileName(folderName, null), currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(1, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		IFileStore directory = getDirectory(serverDirectory, file.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(directory) + " does not exist.", directory.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerOnlyFileFullSyncAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerOnlyFileFullSyncAndDelete() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore serverFile = this.createServerFile("delete.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSyncAndDelete(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(1, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse(
				"Server file: " + EFSUtils.getAbsolutePath(serverFile) + " should be deleted.", serverFile.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * testServerOnlyDirectoryFullSyncAndDelete
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerOnlyDirectoryFullSyncAndDelete() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore serverDir = this.createServerDirectory("delete", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSyncAndDelete(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(1, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());

		assertFalse(
				"Server file: " + EFSUtils.getAbsolutePath(serverDir) + " should be deleted.", serverDir.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerNewerFileFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerNewerFileFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore clientFile = this.createClientFile(
				FileUtil.getRandomFileName(folderName, ".txt"), currentTime - 1000); //$NON-NLS-1$ //$NON-NLS-2$
		IFileStore serverFile = this.createServerFile(clientFile.getName(), currentTime);

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());

		IFileStore clientFileOnServer = getFile(serverDirectory, clientFile.getName());
		assertTrue(clientFileOnServer.fetchInfo().exists());

		IFileStore serverFileOnClient = getFile(clientDirectory, serverFile.getName());
		assertTrue(
				"Server file: " + EFSUtils.getAbsolutePath(serverFile) + " does not exist.", serverFileOnClient.fetchInfo().exists()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testServerNewerDirectoryFullSync()
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testServerNewerDirectoryFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory(folderName, currentTime - 1000); //$NON-NLS-1$
		this.createServerDirectory(folderName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(false, 10);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsDifferFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileCRCsDifferFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile(fileName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testFileCRCsMatchFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testFileCRCsMatchFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientFile(fileName, currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$
		this.createServerFile(fileName, currentTime, "abc123"); //$NON-NLS-1$ //$NON-NLS-2$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testDirectoryCRCsMatchFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testDirectoryCRCsMatchFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		this.createClientDirectory(folderName, currentTime); //$NON-NLS-1$
		this.createServerDirectory(folderName, currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, clientDirectory,
				serverDirectory, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(0, syncManager.getServerFileTransferedCount());
	}

	/**
	 * testDirectoryCRCsMatchFullSync
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 */
	@Test
	public void testSubDirectoryFullSync() throws IOException, CoreException
	{
		long currentTime = new Date().getTime();
		IFileStore sourceDir = this.createClientDirectory(folderName, currentTime); //$NON-NLS-1$
		this.createServerDirectory(folderName, currentTime); //$NON-NLS-1$
		IFileStore destDir = this.createServerDirectory("test/subtest", currentTime); //$NON-NLS-1$
		this.createServerFile("test/subtest/subtest.txt", currentTime); //$NON-NLS-1$

		Synchronizer syncManager = new Synchronizer(true, 0);
		VirtualFileSyncPair[] items = syncManager.getSyncItems(clientManager, serverManager, sourceDir, destDir, null);

		// sync
		syncManager.fullSync(items, null);

		// check client counts
		assertEquals(0, syncManager.getClientDirectoryCreatedCount());
		assertEquals(0, syncManager.getClientDirectoryDeletedCount());
		assertEquals(0, syncManager.getClientFileDeletedCount());
		assertEquals(0, syncManager.getClientFileTransferedCount());

		// check server counts
		assertEquals(0, syncManager.getServerDirectoryCreatedCount());
		assertEquals(0, syncManager.getServerDirectoryDeletedCount());
		assertEquals(0, syncManager.getServerFileDeletedCount());
		assertEquals(1, syncManager.getServerFileTransferedCount());
	}

	protected void assertSyncPairLength(int length, VirtualFileSyncPair[] items)
	{
		if (items.length != length)
		{
			String itemText = "";
			for (int i = 0; i < items.length; i++)
			{
				itemText += items[i].toString() + "\n";
			}
			fail(MessageFormat.format("Expected length of {0}, was {1}.\nList is: {2}", length, items.length, itemText));
		}
	}

}
