/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.ide.syncing.core.old;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;

import com.aptana.core.ILoggable;
import com.aptana.core.ILogger;
import com.aptana.core.util.FileUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.core.io.vfs.IExtendedFileStore;
import com.aptana.ide.syncing.core.SyncingPlugin;

/**
 * @author Kevin Lindsey
 */
public class Synchronizer implements ILoggable
{

	public static final QualifiedName SYNC_IN_PROGRESS = new QualifiedName(Synchronizer.class.getPackage().getName(),
			"SYNC_IN_PROGRESS"); //$NON-NLS-1$

	private static final int DEFAULT_TIME_TOLERANCE = 1000;

	private boolean _useCRC;
	private boolean _includeCloakedFiles = false;
	private long _timeTolerance;

	private int _clientDirectoryCreatedCount;
	private int _clientDirectoryDeletedCount;
	private int _clientFileDeletedCount;
	private int _clientFileTransferedCount;
	private int _serverDirectoryCreatedCount;
	private int _serverDirectoryDeletedCount;
	private int _serverFileDeletedCount;
	private int _serverFileTransferedCount;

	private IConnectionPoint _clientFileManager;
	private IConnectionPoint _serverFileManager;
	private IFileStore _clientFileRoot;
	private IFileStore _serverFileRoot;
	private ISyncEventHandler _eventHandler;
	private ILogger logger;

	private List<IFileStore> _newFilesDownloaded;
	private List<IFileStore> _newFilesUploaded;

	/**
	 * Constructs a Synchronizer with default parameters.
	 */
	public Synchronizer()
	{
		this(false, DEFAULT_TIME_TOLERANCE, false);
	}

	/**
	 * Constructs a Synchronizer with specified CRC flag and tolerance time.
	 * 
	 * @param calculateCrc
	 *            A flag indicating whether two files should be compared by their CRC when their modification times
	 *            match
	 * @param timeTolerance
	 *            The number of seconds a client and server file can differ in their modification times to still be
	 *            considered equal
	 */
	public Synchronizer(boolean calculateCrc, int timeTolerance)
	{
		this(calculateCrc, timeTolerance, false);
	}

	/**
	 * Constructs a Synchronizer with specified CRC flag and tolerance time.
	 * 
	 * @param calculateCrc
	 *            A flag indicating whether two files should be compared by their CRC when their modification times
	 *            match
	 * @param timeTolerance
	 *            The number of milleseconds a client and server file can differ in their modification times to still be
	 *            considered equal
	 * @param includeCloakedFiles
	 * 			  Do we synchronize files marked as cloaked?
	 */
	public Synchronizer(boolean calculateCrc, int timeTolerance, boolean includeCloakedFiles)
	{
		if (timeTolerance < 0)
		{
			// makes sure time is positive
			timeTolerance = -timeTolerance;
		}

		this._useCRC = calculateCrc;
		this._includeCloakedFiles = includeCloakedFiles;
		this._timeTolerance = timeTolerance;
		_newFilesDownloaded = new ArrayList<IFileStore>();
		_newFilesUploaded = new ArrayList<IFileStore>();
	}

	/**
	 * Logs a message.
	 * 
	 * @param message
	 *            the message to be logged
	 */
	protected void log(String message)
	{
		if (this.logger != null)
		{
			this.logger.logInfo(message, null);
		}
	}

	/**
	 * Convert the full path of the specified file into a canonical form. This will remove the base directory set by the
	 * file's server and it will convert all '\' characters to '/' characters.
	 * 
	 * @param file
	 *            The file to use when computing the canonical path
	 * @return the file's canonical path
	 */
	public static String getCanonicalPath(IFileStore root, IFileStore file)
	{
		String basePath = null;
		String result = null;

		try
		{
			basePath = EFSUtils.getAbsolutePath(root);

			result = EFSUtils.getAbsolutePath(file).substring(basePath.length());

			if (result.indexOf('\\') != -1)
			{
				result = result.replace('\\', '/');
			}

			if (result.startsWith("/")) //$NON-NLS-1$
			{
				result = result.substring(1);
			}
		}
		catch (StringIndexOutOfBoundsException e)
		{
			throw new IllegalArgumentException(StringUtil.format(Messages.Synchronizer_FileNotContained, new String[] {
					EFSUtils.getAbsolutePath(file), basePath }));
		}

		return result;
	}

	/**
	 * Returns the number of directories that were created on the client.
	 * 
	 * @return the number of directories created on the client
	 */
	public int getClientDirectoryCreatedCount()
	{
		return this._clientDirectoryCreatedCount;
	}

	/**
	 * Returns the number of directories that were deleted from the client.
	 * 
	 * @return the number of directories deleted from the client
	 */
	public int getClientDirectoryDeletedCount()
	{
		return this._clientDirectoryDeletedCount;
	}

	/**
	 * Returns the number of files that were deleted from the client.
	 * 
	 * @return the number of files deleted from the client
	 */
	public int getClientFileDeletedCount()
	{
		return this._clientFileDeletedCount;
	}

	/**
	 * Returns the number of files that were transferred to the server.
	 * 
	 * @return the number of files transferred to the server
	 */
	public int getClientFileTransferedCount()
	{
		return this._clientFileTransferedCount;
	}

	/**
	 * Gets the current sync event handler.
	 * 
	 * @return the event handler for syncing
	 */
	public ISyncEventHandler getEventHandler()
	{
		return this._eventHandler;
	}

	/**
	 * Sets the current sync event handler
	 * 
	 * @param eventHandler
	 *            the event handler for syncing
	 */
	public void setEventHandler(ISyncEventHandler eventHandler)
	{
		this._eventHandler = eventHandler;
	}

	/**
	 * Returns the number of directories that were created on the server.
	 * 
	 * @return the number of directories created on the server
	 */
	public int getServerDirectoryCreatedCount()
	{
		return this._serverDirectoryCreatedCount;
	}

	/**
	 * Returns the number of directories that were deleted from the server.
	 * 
	 * @return the number of directories deleted from the server
	 */
	public int getServerDirectoryDeletedCount()
	{
		return this._serverDirectoryDeletedCount;
	}

	/**
	 * Returns the number of files that were deleted from the server.
	 * 
	 * @return the number of files deleted from the server
	 */
	public int getServerFileDeletedCount()
	{
		return this._serverFileDeletedCount;
	}

	/**
	 * Returns the number of files that were transferred to the client.
	 * 
	 * @return the number of files that were transferred to the client
	 */
	public int getServerFileTransferedCount()
	{
		return this._serverFileTransferedCount;
	}

	public IFileStore[] getNewFilesDownloaded()
	{
		return _newFilesDownloaded.toArray(new IFileStore[_newFilesDownloaded.size()]);
	}

	public IFileStore[] getNewFilesUploaded()
	{
		return _newFilesUploaded.toArray(new IFileStore[_newFilesUploaded.size()]);
	}

	public void setClientFileRoot(IFileStore client)
	{
		_clientFileRoot = client;
	}

	public void setServerFileRoot(IFileStore server)
	{
		_serverFileRoot = server;
	}

	/**
	 * Gets the list of items to sync.
	 * 
	 * @param client
	 *            the client
	 * @param server
	 *            the server
	 * @return an array of item pairs to sync
	 * @throws IOException
	 * @throws VirtualFileManagerException
	 * @throws ConnectionException
	 * @throws CoreException
	 */
	public VirtualFileSyncPair[] getSyncItems(IConnectionPoint clientPoint, IConnectionPoint serverPoint,
			IFileStore client, IFileStore server, IProgressMonitor monitor) throws IOException, CoreException
	{
		// store references to file managers
		setClientFileManager(clientPoint);
		setServerFileManager(serverPoint);
		setClientFileRoot(client);
		setServerFileRoot(server);

		IFileStore[] clientFiles = new IFileStore[0];
		IFileStore[] serverFiles = new IFileStore[0];

		try
		{
			setClientEventHandler(client, server);

			if (!client.fetchInfo().isDirectory() || !server.fetchInfo().isDirectory())
			{
				if (client.fetchInfo().exists())
				{
					clientFiles = new IFileStore[] { client };
				}

				if (server.fetchInfo().exists())
				{
					serverFiles = new IFileStore[] { server };
				}
			}
			else
			{
				// get the complete file listings for the client and server
				log(FileUtil.NEW_LINE + "Gathering list of source files from '" + client.toString() + "'. ");
				clientFiles = EFSUtils.getFiles(client, true, _includeCloakedFiles, monitor);
				log("Completed.");
				log(FileUtil.NEW_LINE + "Gathering list of destination files from '" + server.toString() + "'. ");
				serverFiles = EFSUtils.getFiles(server, true, _includeCloakedFiles, monitor);
				log("Completed.");
				log(FileUtil.NEW_LINE + "File listing complete.");
			}
		}
		finally
		{
			// we just throw exceptions back up the tree
			removeClientEventHandler(client, server);
		}

		if (!syncContinue())
		{
			return null;
		}

		return createSyncItems(clientFiles, serverFiles);
	}

	/**
	 * @param clientFiles
	 * @param serverFiles
	 * @return VirtualFileSyncPair[]
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 * @throws CoreException
	 */
	public VirtualFileSyncPair[] createSyncItems(IFileStore[] clientFiles, IFileStore[] serverFiles)
			throws IOException, CoreException
	{
		log(FileUtil.NEW_LINE + "Generating comparison.");

		Map<String, VirtualFileSyncPair> fileList = new HashMap<String, VirtualFileSyncPair>();

		// reset statistics and clear lists
		this.reset();

		// add all client files by default
		for (int i = 0; i < clientFiles.length; i++)
		{
			if (!syncContinue())
				return null;

			IFileStore clientFile = clientFiles[i];
			if (clientFile.fetchInfo().getAttribute(EFS.ATTRIBUTE_SYMLINK))
				continue;

			String relativePath = getCanonicalPath(_clientFileRoot, clientFile);
			VirtualFileSyncPair item = new VirtualFileSyncPair(clientFile, null, relativePath, SyncState.ClientItemOnly);
			fileList.put(item.getRelativePath(), item);
		}

		// remove matching server files with the same modification date/time
		for (int i = 0; i < serverFiles.length; i++)
		{
			if (!syncContinue())
				return null;

			IFileStore serverFile = serverFiles[i];
			String relativePath = getCanonicalPath(_serverFileRoot, serverFile);

			logDebug(FileUtil.NEW_LINE + "Comparing '" + relativePath + "' with file from destination. ");

			if (!fileList.containsKey(relativePath)) // Server only
			{
				if (serverFile.fetchInfo().getAttribute(EFS.ATTRIBUTE_SYMLINK))
					continue;
				VirtualFileSyncPair item = new VirtualFileSyncPair(null, serverFile, relativePath,
						SyncState.ServerItemOnly);
				fileList.put(relativePath, item);
				logDebug("Item not on destination.");
				continue;
			}

			// Client and server
			// get client sync item already in our file list
			VirtualFileSyncPair item = fileList.get(relativePath);

			// associate this server file with that sync item
			item.setDestinationFile(serverFile);

			if (item.getSourceFile().fetchInfo().isDirectory() != serverFile.fetchInfo().isDirectory())
			{
				// this only occurs if one file is a directory and the other
				// is not a directory
				item.setSyncState(SyncState.IncompatibleFileTypes);
				logDebug("Incompatible types.");
				continue;
			}

			if (serverFile.fetchInfo().isDirectory())
			{
				fileList.remove(relativePath);
				logDebug("Directory.");
				continue;
			}

			// calculate modification time difference, taking server
			// offset into account
			long serverFileTime = serverFile.fetchInfo(IExtendedFileStore.DETAILED, null).getLastModified();
			long clientFileTime = item.getSourceFile().fetchInfo(IExtendedFileStore.DETAILED, null).getLastModified();
			long timeDiff = serverFileTime - clientFileTime;

			logDebug("Source modified: " + clientFileTime + " Destination modified: " + serverFileTime + ". ");

			// check modification date
			if (-this._timeTolerance <= timeDiff && timeDiff <= this._timeTolerance)
			{
				if (this._useCRC && !serverFile.fetchInfo().isDirectory())
				{
					item.setSyncState(this.compareCRC(item));
				}
				else
				{
					item.setSyncState(SyncState.ItemsMatch);
					logDebug("Items identical.");
				}
			}
			else
			{
				if (timeDiff < 0)
				{
					item.setSyncState(SyncState.ClientItemIsNewer);
					logDebug("Source Newer by " + Math.round(Math.abs(timeDiff/1000)) + " seconds.");
				}
				else
				{
					item.setSyncState(SyncState.ServerItemIsNewer);
					logDebug("Destination Newer by " + Math.round(timeDiff/1000) + " seconds.");
				}
			}
		}

		// sort items
		Set<String> keySet = fileList.keySet();
		String[] keys = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keys);

		// create modifiable list
		VirtualFileSyncPair[] syncItems = new VirtualFileSyncPair[keys.length];
		for (int i = 0; i < keys.length; i++)
		{
			syncItems[i] = fileList.get(keys[i]);
		}
		// long end = System.currentTimeMillis();
		// System.out.println(end - start);

		// return results
		return syncItems;
	}

	/**
	 * @param client
	 * @param server
	 */
	private void setClientEventHandler(IFileStore client, IFileStore server)
	{
		// client.getFileManager().setEventHandler(this._eventHandler);
		// server.getFileManager().setEventHandler(this._eventHandler);
	}

	/**
	 * @param client
	 * @param server
	 */
	private void removeClientEventHandler(IFileStore client, IFileStore server)
	{
		// client.getFileManager().setEventHandler(null);
		// server.getFileManager().setEventHandler(null);
	}

	/**
	 * getTimeTolerance
	 * 
	 * @return Returns the timeTolerance.
	 */
	public long getTimeTolerance()
	{
		return this._timeTolerance;
	}

	/**
	 * setTimeTolerance
	 * 
	 * @param timeTolerance
	 *            The timeTolerance to set.
	 */
	public void setTimeTolerance(int timeTolerance)
	{
		this._timeTolerance = timeTolerance;
	}

	/**
	 * setCalculateCrc
	 * 
	 * @param calculateCrc
	 *            The calculateCrc to set.
	 */
	public void setUseCRC(boolean calculateCrc)
	{
		this._useCRC = calculateCrc;
	}

	/**
	 * isCalculateCrc
	 * 
	 * @return Returns the calculateCrc.
	 */
	public boolean getUseCRC()
	{
		return this._useCRC;
	}

	/**
	 * compareCRC
	 * 
	 * @param item
	 * @return SyncState
	 * @throws CoreException
	 */
	private int compareCRC(VirtualFileSyncPair item) throws IOException, CoreException
	{
		InputStream clientStream = item.getSourceInputStream();
		InputStream serverStream = item.getDestinationInputStream();
		int result;

		if (clientStream != null && serverStream != null)
		{
			// get individual CRC's
			long clientCRC = getCRC(clientStream);
			long serverCRC = getCRC(serverStream);

			// close streams
			try
			{
				clientStream.close();
				serverStream.close();
			}
			catch (IOException e)
			{
				SyncingPlugin.logError(StringUtil.format(
						Messages.Synchronizer_ErrorClosingStreams, item.getRelativePath()), e);
			}

			result = (clientCRC == serverCRC) ? SyncState.ItemsMatch : SyncState.CRCMismatch;
		}
		else
		{
			// NOTE: clientStream can only equal serverStream if both are null,
			// so we assume the files match in that case
			result = (clientStream == serverStream) ? SyncState.ItemsMatch : SyncState.CRCMismatch;
		}

		return result;
	}

	/**
	 * getCRC
	 * 
	 * @param stream
	 * @return CRC
	 */
	private long getCRC(InputStream stream)
	{
		CRC32 crc = new CRC32();

		try
		{
			byte[] buffer = new byte[1024];
			int length;

			while ((length = stream.read(buffer)) != -1)
			{
				crc.update(buffer, 0, length);
			}
		}
		catch (IOException e)
		{
			SyncingPlugin.logError(Messages.Synchronizer_ErrorRetrievingCRC, e);

		}

		return crc.getValue();
	}

	// public void cancelAllOperations()
	// {
	// if (this._clientFileManager != null)
	// {
	// this._clientFileManager.cancel();
	// }
	// if (this._serverFileManager != null)
	// {
	// this._serverFileManager.cancel();
	// }
	// }

	/**
	 * Download to the client all files on the server that are newer or that only exist on the server
	 * 
	 * @param fileList
	 * @return success
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	public boolean download(VirtualFileSyncPair[] fileList, IProgressMonitor monitor) throws CoreException
	{
		return this.downloadAndDelete(fileList, false, monitor);
	}

	/**
	 * Download to the client all files on the server that are newer or delete files on the client that don't exist on
	 * the server
	 * 
	 * @param fileList
	 * @return success
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	public boolean downloadAndDelete(VirtualFileSyncPair[] fileList, IProgressMonitor monitor) throws CoreException
	{
		return this.downloadAndDelete(fileList, true, monitor);
	}

	/**
	 * downloadAndDelete
	 * 
	 * @param fileList
	 * @param delete
	 * @return success
	 * @throws CoreException
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	public boolean downloadAndDelete(VirtualFileSyncPair[] fileList, boolean delete, IProgressMonitor monitor)
			throws CoreException
	{
		checkFileManagers();

		logBeginDownloading();

		boolean result = true;
		int totalItems = fileList.length;
		//IConnectionPoint client = getClientFileManager();

		this.reset();

		FILE_LOOP: for (int i = 0; i < fileList.length; i++)
		{
			final VirtualFileSyncPair item = fileList[i];
			final IFileStore clientFile = item.getSourceFile();
			final IFileStore serverFile = item.getDestinationFile();

			setSyncItemDirection(item, false, false);

			// fire event
			if (!syncEvent(item, i, totalItems))
			{
				delete = false;
				break;
			}

			switch (item.getSyncState())
			{
				case SyncState.ClientItemOnly:
					// only exists on client; checks if it needs to be deleted
					if (delete)
					{
						// Need to query first because deletion makes isDirectory always return false
						boolean wasDirectory = clientFile.fetchInfo().isDirectory();
						clientFile.delete(EFS.NONE, null);
						// client.deleteFile(clientFile);
						if (wasDirectory)
						{
							this._clientDirectoryDeletedCount++;
						}
						else
						{
							this._clientFileDeletedCount++;
						}
					}
					syncDone(item);
					break;

				case SyncState.ServerItemOnly:
					final IFileStore targetClientFile = EFSUtils.createFile(_serverFileRoot, item.getDestinationFile(), _clientFileRoot);

					if (serverFile.fetchInfo().isDirectory())
					{
						logCreatedDirectory(targetClientFile);

						if (!targetClientFile.fetchInfo().exists())
						{
							targetClientFile.mkdir(EFS.NONE, null);
							// createVirtualDirectory(clientPath);
							// client.createLocalDirectory(targetClientFile);
							this._clientDirectoryCreatedCount++;
							_newFilesDownloaded.add(targetClientFile);
						}

						logSuccess();
						syncDone(item);
					}
					else
					{
						// targetClientFile = client.getRoot().createVirtualFile(clientPath);
						logDownloading(serverFile);
						try
						{
							if (EFSUtils.copyFile(serverFile, targetClientFile, monitor))
							{
								Synchronizer.this._serverFileTransferedCount++;
								_newFilesDownloaded.add(targetClientFile);

								logSuccess();
								syncDone(item);
							}
						}
						catch (CoreException e)
						{
							logError(e);
							if (!syncError(item, e))
							{
								result = false;
								break FILE_LOOP;
							}
						}
					}
					break;

				case SyncState.ServerItemIsNewer:
				case SyncState.CRCMismatch:
					// exists on both sides, but the server item is newer
					logDownloading(serverFile);
					if (serverFile.fetchInfo().isDirectory())
					{
						try
						{
							EFSUtils.setModificationTime(serverFile, clientFile);
						}
						catch (CoreException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						logSuccess();
						syncDone(item);
					}
					else
					{
						try
						{
							if (EFSUtils.copyFile(serverFile, clientFile, monitor))
							{
								Synchronizer.this._serverFileTransferedCount++;
								logSuccess();
								syncDone(item);
							}
						}
						catch (CoreException e)
						{
							logError(e);
							if (!syncError(item, e))
							{
								result = false;
								break FILE_LOOP;
							}
						}
					}
					break;

				default:
					syncDone(item);
					break;
			}
		}

		return result;
	}

	/**
	 * fullSync
	 * 
	 * @param fileList
	 * @return success
	 */
	public boolean fullSync(VirtualFileSyncPair[] fileList, IProgressMonitor monitor)
	{
		return this.fullSyncAndDelete(fileList, false, false, monitor);
	}

	/**
	 * fullSyncAndDelete
	 * 
	 * @param fileList
	 * @return success
	 */
	public boolean fullSyncAndDelete(VirtualFileSyncPair[] fileList, IProgressMonitor monitor)
	{
		return this.fullSyncAndDelete(fileList, true, true, monitor);
	}

	/**
	 * fullSyncAndDelete
	 * 
	 * @param fileList
	 * @param delete
	 * @return success
	 */
	public boolean fullSyncAndDelete(VirtualFileSyncPair[] fileList, boolean deleteLocal, boolean deleteRemote,
			IProgressMonitor monitor)
	{
		logBeginFullSyncing();

		// assume we'll be successful
		boolean result = true;
		int totalItems = fileList.length;

		// reset stats
		this.reset();

		// process all items in our list
		FILE_LOOP: for (int i = 0; i < fileList.length; i++)
		{
			final VirtualFileSyncPair item = fileList[i];
			final IFileStore clientFile = item.getSourceFile();
			final IFileStore serverFile = item.getDestinationFile();

			try
			{

				setSyncItemDirection(item, false, true);

				// fire event
				if (!syncEvent(item, i, totalItems))
				{
					result = false;
					break FILE_LOOP;
				}

				switch (item.getSyncState())
				{
					case SyncState.ClientItemIsNewer:
						// item exists on both ends, but the client one is newer
						logUploading(serverFile);
						if (clientFile.fetchInfo().isDirectory())
						{
							EFSUtils.setModificationTime(clientFile, serverFile);
							logSuccess();
							syncDone(item);
						}
						else
						{
							try
							{
								if (EFSUtils.copyFile(clientFile, serverFile, monitor))
								{
									Synchronizer.this._clientFileTransferedCount++;

									logSuccess();
									syncDone(item);
								}
							}
							catch (CoreException e)
							{
								logError(e);

								if (!syncError(item, e))
								{
									result = false;
									break FILE_LOOP;
								}
							}

						}
						break;

					case SyncState.ClientItemOnly:
						// only exists on client
						if (deleteLocal)
						{
							// need to query first because deletion causes isDirectory to always return false
							boolean wasDirectory = clientFile.fetchInfo().isDirectory();
							// deletes the item
							clientFile.delete(EFS.NONE, null); // .deleteFile(clientFile);
							if (wasDirectory)
							{
								this._clientDirectoryDeletedCount++;
							}
							else
							{
								this._clientFileDeletedCount++;
							}
							logSuccess();
							syncDone(item);
						}
						else
						{
							// creates the item on server
							final IFileStore targetServerFile = EFSUtils.createFile(_clientFileRoot, item.getSourceFile(), _serverFileRoot);

							if (clientFile.fetchInfo().isDirectory())
							{
								logCreatedDirectory(targetServerFile);

								if (!targetServerFile.fetchInfo().exists())
								{
									targetServerFile.mkdir(EFS.NONE, null); // =
									// server.createVirtualDirectory(serverPath);
									// server.createLocalDirectory(targetServerFile);
									this._serverDirectoryCreatedCount++;
									_newFilesUploaded.add(targetServerFile);
								}

								logSuccess();
								syncDone(item);
							}
							else
							{
								// targetServerFile = server.createVirtualFile(serverPath);
								logUploading(clientFile);
								try
								{
									if (EFSUtils.copyFile(clientFile, targetServerFile, monitor))
									{
										Synchronizer.this._clientFileTransferedCount++;
										_newFilesUploaded.add(targetServerFile);

										logSuccess();
										syncDone(item);
									}
								}
								catch (CoreException e)
								{
									logError(e);

									if (!syncError(item, e))
									{
										result = false;
										break FILE_LOOP;
									}
								}
							}
						}
						break;

					case SyncState.ServerItemIsNewer:
						// item exists on both ends, but the server one is newer
						logDownloading(clientFile);
						if (serverFile.fetchInfo().isDirectory())
						{
							// just needs to set the modification time for directory
							EFSUtils.setModificationTime(serverFile, clientFile);

							logSuccess();
							syncDone(item);
						}
						else
						{
							try
							{
								if (EFSUtils.copyFile(serverFile, clientFile, monitor))
								{
									Synchronizer.this._serverFileTransferedCount++;
									logSuccess();
									syncDone(item);
								}
							}
							catch (CoreException e)
							{
								logError(e);

								if (!syncError(item, e))
								{
									result = false;
									break FILE_LOOP;
								}
							}

						}
						break;

					case SyncState.ServerItemOnly:
						// only exists on client
						if (deleteRemote)
						{
							// need to query first because deletion causes isDirectory to always return false
							boolean wasDirectory = serverFile.fetchInfo().isDirectory();
							// deletes the item
							serverFile.delete(EFS.NONE, null); // server.deleteFile(serverFile);
							if (wasDirectory)
							{
								this._serverDirectoryDeletedCount++;
							}
							else
							{
								this._serverFileDeletedCount++;
							}
							logSuccess();
							syncDone(item);
						}
						else
						{
							// creates the item on client
							final IFileStore targetClientFile = EFSUtils.createFile(_serverFileRoot, item.getDestinationFile(), _clientFileRoot);

							if (serverFile.fetchInfo().isDirectory())
							{
								logCreatedDirectory(targetClientFile);

								if (!targetClientFile.fetchInfo().exists())
								{
									targetClientFile.mkdir(EFS.NONE, null); // =
									// client.createVirtualDirectory(clientPath);
									// client.createLocalDirectory(targetClientFile);
									this._clientDirectoryCreatedCount++;
									_newFilesDownloaded.add(targetClientFile);
								}

								logSuccess();
								syncDone(item);
							}
							else
							{
								// targetClientFile = client.createVirtualFile(clientPath);
								logDownloading(targetClientFile);

								try
								{
									if (EFSUtils.copyFile(serverFile, targetClientFile, monitor))
									{
										Synchronizer.this._serverFileTransferedCount++;
										_newFilesDownloaded.add(targetClientFile);

										logSuccess();
										syncDone(item);
									}
								}
								catch (CoreException e)
								{
									logError(e);

									if (!syncError(item, e))
									{
										result = false;
										break FILE_LOOP;
									}
								}
							}
						}
						break;

					case SyncState.CRCMismatch:
						result = false;
						SyncingPlugin.logError(StringUtil.format(
								Messages.Synchronizer_FullSyncCRCMismatches, item.getRelativePath()), null);
						if (!syncError(item, null))
						{
							break FILE_LOOP;
						}
						break;

					case SyncState.Ignore:
						// ignore this file
						break;

					default:
						break;
				}
			}
			catch (Exception ex)
			{
				SyncingPlugin.logError(Messages.Synchronizer_ErrorDuringSync, ex);
				result = false;

				if (!syncError(item, ex))
				{
					break FILE_LOOP;
				}
			}
		}

		return result;
	}

	/**
	 * resetStats
	 */
	private void reset()
	{
		this._clientDirectoryCreatedCount = 0;
		this._clientDirectoryDeletedCount = 0;
		this._clientFileDeletedCount = 0;
		this._clientFileTransferedCount = 0;

		this._serverDirectoryCreatedCount = 0;
		this._serverDirectoryDeletedCount = 0;
		this._serverFileDeletedCount = 0;
		this._serverFileTransferedCount = 0;

		this._newFilesDownloaded.clear();
		this._newFilesUploaded.clear();
	}

	/**
	 * Upload to the server all files on the client that are newer or that only exist on the client
	 * 
	 * @param fileList
	 * @return success
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	public boolean upload(VirtualFileSyncPair[] fileList, IProgressMonitor monitor) throws CoreException
	{
		return this.uploadAndDelete(fileList, false, monitor);
	}

	/**
	 * Upload to the server all files on the client that are newer or delete files on the server that don't exist on the
	 * client
	 * 
	 * @param fileList
	 * @return success
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	public boolean uploadAndDelete(VirtualFileSyncPair[] fileList, IProgressMonitor monitor) throws CoreException
	{
		return this.uploadAndDelete(fileList, true, monitor);
	}

	/**
	 * uploadAndDelete
	 * 
	 * @param fileList
	 * @param delete
	 * @return success
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	public boolean uploadAndDelete(VirtualFileSyncPair[] fileList, boolean delete, IProgressMonitor monitor)
			throws CoreException
	{
		checkFileManagers();
		logBeginUploading();

		//IConnectionPoint server = getServerFileManager();
		boolean result = true;
		int totalItems = fileList.length;

		this.reset();

		FILE_LOOP: for (int i = 0; i < fileList.length; i++)
		{
			final VirtualFileSyncPair item = fileList[i];
			final IFileStore clientFile = item.getSourceFile();
			final IFileStore serverFile = item.getDestinationFile();

			setSyncItemDirection(item, true, false);

			// fire event
			if (!syncEvent(item, i, totalItems))
			{
				result = false;
				break;
			}

			switch (item.getSyncState())
			{
				case SyncState.ClientItemOnly:
					// only exists on client; creates the item on server
					final IFileStore targetServerFile = EFSUtils.createFile(_clientFileRoot, item.getSourceFile(), _serverFileRoot);

					if (clientFile.fetchInfo().isDirectory())
					{
						// targetServerFile.mkdir(EFS.NONE, null); // = server.createVirtualDirectory(serverPath);

						if (!targetServerFile.fetchInfo().exists())
						{
							targetServerFile.mkdir(EFS.NONE, null); // server.createLocalDirectory(targetServerFile);
							this._serverDirectoryCreatedCount++;
							_newFilesUploaded.add(targetServerFile);
						}

						syncDone(item);
					}
					else
					{
						// targetServerFile = server.createVirtualFile(serverPath);

						logUploading(clientFile);

						try
						{
							if (EFSUtils.copyFile(clientFile, targetServerFile, monitor))
							{
								Synchronizer.this._clientFileTransferedCount++;
								_newFilesUploaded.add(targetServerFile);
								logSuccess();
								syncDone(item);
							}
						}
						catch (CoreException e)
						{
							logError(e);

							if (!syncError(item, e))
							{
								result = false;
								break FILE_LOOP;
							}
						}

					}
					break;

				case SyncState.ServerItemOnly:
					// only exists on server; checks if it needs to be deleted
					if (delete)
					{
						// Need to query if directory first because deletion makes isDirectory always return false.
						boolean wasDirectory = serverFile.fetchInfo().isDirectory();
						serverFile.delete(EFS.NONE, monitor); // server.deleteFile(serverFile);
						if (wasDirectory)
						{
							this._serverDirectoryDeletedCount++;
						}
						else
						{
							this._serverFileDeletedCount++;
						}
					}
					syncDone(item);
					break;

				case SyncState.ClientItemIsNewer:
				case SyncState.CRCMismatch:
					// exists on both sides, but the client item is newer
					logUploading(clientFile);
					if (clientFile.fetchInfo().isDirectory())
					{
						// just needs to set the modification time for directory
						try
						{
							EFSUtils.setModificationTime(clientFile, serverFile);
						}
						catch (CoreException e)
						{
							logError(e);

							if (!syncError(item, e))
							{
								result = false;
								break FILE_LOOP;
							}
						}

						logSuccess();
						syncDone(item);
					}
					else
					{
						try
						{
							if (EFSUtils.copyFile(clientFile, serverFile, monitor))
							{
								Synchronizer.this._clientFileTransferedCount++;
								logSuccess();
								syncDone(item);
							}
						}
						catch (CoreException e)
						{
							logError(e);

							if (!syncError(item, e))
							{
								result = false;
								break FILE_LOOP;
							}
						}

					}
					break;

				default:
					syncDone(item);
					break;
			}
		}

		return result;
	}

	private static void setSyncItemDirection(VirtualFileSyncPair item, boolean upload, boolean full)
	{
		int direction = VirtualFileSyncPair.Direction_None;
		switch (item.getSyncState())
		{
			case SyncState.Unknown:
			case SyncState.Ignore:
			case SyncState.ItemsMatch:
			case SyncState.IncompatibleFileTypes:
				break;
			case SyncState.CRCMismatch:
				if (upload)
				{
					direction = VirtualFileSyncPair.Direction_ClientToServer;
				}
				else if (!full)
				{
					direction = VirtualFileSyncPair.Direction_ServerToClient;
				}
				break;
			case SyncState.ClientItemIsNewer:
			case SyncState.ClientItemOnly:
				if (upload || full)
				{
					direction = VirtualFileSyncPair.Direction_ClientToServer;
				}
				break;
			case SyncState.ServerItemIsNewer:
			case SyncState.ServerItemOnly:
				if (!upload || full)
				{
					direction = VirtualFileSyncPair.Direction_ServerToClient;
				}
				break;
		}
		item.setSyncDirection(direction);
	}

	/**
	 * @return Returns the clientFileManager.
	 */
	public IConnectionPoint getClientFileManager()
	{
		return this._clientFileManager;
	}

	/**
	 * @param fileManager
	 *            The clientFileManager to set.
	 */
	public void setClientFileManager(IConnectionPoint fileManager)
	{
		this._clientFileManager = fileManager;
	}

	/**
	 * @return Returns the serverFileManager.
	 */
	public IConnectionPoint getServerFileManager()
	{
		return this._serverFileManager;
	}

	/**
	 * @param fileManager
	 *            The serverFileManager to set.
	 */
	public void setServerFileManager(IConnectionPoint fileManager)
	{
		this._serverFileManager = fileManager;
	}

	/**
	 * Resets time tolerance.
	 */
	public void resetTimeTolerance()
	{
		this._timeTolerance = DEFAULT_TIME_TOLERANCE;
	}

	/**
	 * @see com.aptana.ide.core.ILoggable#getLogger()
	 */
	public ILogger getLogger()
	{
		return this.logger;
	}

	/**
	 * @see com.aptana.ide.core.ILoggable#setLogger(com.aptana.ide.core.ILogger)
	 */
	public void setLogger(ILogger logger)
	{
		this.logger = logger;
	}

	private void checkFileManagers()
	{
		if (getClientFileManager() == null)
		{
			throw new NullPointerException(Messages.Synchronizer_ClientFileManagerCannotBeNull);
		}
		if (getServerFileManager() == null)
		{
			throw new NullPointerException(Messages.Synchronizer_ServerFileManagerCannotBeNull);
		}
	}

	private void logBeginDownloading()
	{
		log(FileUtil.NEW_LINE + FileUtil.NEW_LINE
				+ StringUtil.format(Messages.Synchronizer_BeginningDownload, getTimestamp()));
	}

	private void logBeginFullSyncing()
	{
		log(FileUtil.NEW_LINE + FileUtil.NEW_LINE
				+ StringUtil.format(Messages.Synchronizer_BeginningFullSync, getTimestamp()));
	}

	private void logBeginUploading()
	{
		log(FileUtil.NEW_LINE + FileUtil.NEW_LINE
				+ StringUtil.format(Messages.Synchronizer_BeginningUpload, getTimestamp()));
	}

	private void logCreatedDirectory(IFileStore file)
	{
		log(FileUtil.NEW_LINE
				+ StringUtil.format(Messages.Synchronizer_CreatedDirectory, EFSUtils.getAbsolutePath(file)));
	}

	private void logDownloading(IFileStore file)
	{
		log(FileUtil.NEW_LINE + StringUtil.format(Messages.Synchronizer_Downloading, EFSUtils.getAbsolutePath(file)));
	}

	private void logDebug(String message)
	{
		//log(message);
	}

	private void logError(Exception e)
	{
		if (this.logger != null)
		{
			if(e.getCause() != null) {
				log(StringUtil.format(Messages.Synchronizer_Error, e.getCause().getLocalizedMessage()));
			}
			else {
				log(StringUtil.format(Messages.Synchronizer_Error, e.getLocalizedMessage()));				
			}
		}
	}

	private void logSuccess()
	{
		log(Messages.Synchronizer_Success);
	}

	private void logUploading(IFileStore file)
	{
		log(FileUtil.NEW_LINE + StringUtil.format(Messages.Synchronizer_Uploading, EFSUtils.getAbsolutePath(file)));
	}

	private void syncDone(VirtualFileSyncPair item)
	{
		if (this._eventHandler != null)
		{
			this._eventHandler.syncDone(item);
		}
	}

	private boolean syncError(VirtualFileSyncPair item, Exception e)
	{
		return this._eventHandler == null || this._eventHandler.syncErrorEvent(item, e);
	}

	private boolean syncEvent(VirtualFileSyncPair item, int index, int totalItems)
	{
		return this._eventHandler == null || this._eventHandler.syncEvent(item, index, totalItems);
	}

	private boolean syncContinue()
	{
		return this._eventHandler == null || this._eventHandler.syncContinue();
	}

	private static String getTimestamp()
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
		return df.format(d);
	}

	public void disconnect()
	{
		try
		{
			getClientFileManager().disconnect(null);
			getServerFileManager().disconnect(null);
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
