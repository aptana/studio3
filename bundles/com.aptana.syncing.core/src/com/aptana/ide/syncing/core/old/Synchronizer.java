/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.io.efs.SyncUtils;
import com.aptana.core.io.vfs.IExtendedFileInfo;
import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.io.vfs.Policy;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.FileUtil;
import com.aptana.filewatcher.FileWatcher;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.preferences.PermissionDirection;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.aptana.ide.syncing.core.SyncingPlugin;

/**
 * @author Kevin Lindsey
 */
@SuppressWarnings("restriction")
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
	 *            The number of milliseconds a client and server file can differ in their modification times to still be
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
	 *            The number of milliseconds a client and server file can differ in their modification times to still be
	 *            considered equal
	 * @param includeCloakedFiles
	 *            Do we synchronize files marked as cloaked?
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
			if (basePath == null)
			{
				return null;
			}

			String filePath = EFSUtils.getAbsolutePath(file);
			if (filePath == null)
			{
				return null;
			}
			result = filePath.substring(basePath.length());
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
			throw new IllegalArgumentException(MessageFormat.format(Messages.Synchronizer_FileNotContained,
					EFSUtils.getAbsolutePath(file), basePath));
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
			IFileStore client, IFileStore server, IProgressMonitor monitor) throws CoreException
	{
		// store references to file managers
		setClientFileManager(clientPoint);
		setServerFileManager(serverPoint);
		setClientFileRoot(client);
		setServerFileRoot(server);

		IFileStore[] clientFiles = new IFileStore[0];
		IFileStore[] serverFiles = new IFileStore[0];
		IFileInfo clientInfo = client.fetchInfo();
		if (!clientInfo.exists())
		{
			throw new CoreException(new Status(IStatus.ERROR, SyncingPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.Synchronizer_ERR_RootNotExist, client.toString())));
		}
		IFileInfo serverInfo = server.fetchInfo();
		if (!serverInfo.exists())
		{
			throw new CoreException(new Status(IStatus.ERROR, SyncingPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.Synchronizer_ERR_RootNotExist, server.toString())));
		}
		try
		{
			setClientEventHandler(client, server);

			if (!clientInfo.isDirectory() || !serverInfo.isDirectory())
			{
				if (clientInfo.exists())
				{
					clientFiles = new IFileStore[] { client };
				}

				if (serverInfo.exists())
				{
					serverFiles = new IFileStore[] { server };
				}
			}
			else
			{
				// get the complete file listings for the client and server
				log(FileUtil.NEW_LINE);
				log(MessageFormat.format(Messages.Synchronizer_Gathering_Source, new Object[] { client.toString() }));

				long start = System.currentTimeMillis();
				clientFiles = EFSUtils.getFiles(client, true, _includeCloakedFiles, monitor);
				log(MessageFormat.format(Messages.Synchronizer_Completed, System.currentTimeMillis() - start));

				start = System.currentTimeMillis();
				log(FileUtil.NEW_LINE);
				log(MessageFormat.format(Messages.Synchronizer_Gathering_Destination,
						new Object[] { server.toString() }));
				serverFiles = EFSUtils.getFiles(server, true, _includeCloakedFiles, monitor);
				log(MessageFormat.format(Messages.Synchronizer_Completed, System.currentTimeMillis() - start));

				log(FileUtil.NEW_LINE);
				log(Messages.Synchronizer_Listing_Complete);
			}
		}
		finally
		{
			// we just throw exceptions back up the tree
			removeClientEventHandler(client, server);
		}

		if (!syncContinue(monitor))
		{
			return null;
		}

		return createSyncItems(clientFiles, serverFiles, monitor);
	}

	/**
	 * @param clientFiles
	 * @param serverFiles
	 * @param monitor
	 *            TODO
	 * @return VirtualFileSyncPair[]
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 * @throws CoreException
	 */
	public VirtualFileSyncPair[] createSyncItems(IFileStore[] clientFiles, IFileStore[] serverFiles,
			IProgressMonitor monitor) throws CoreException
	{
		log(FileUtil.NEW_LINE + Messages.Synchronizer_Generating_Comparison);

		Map<String, VirtualFileSyncPair> fileList = new HashMap<String, VirtualFileSyncPair>();

		// reset statistics and clear lists
		this.reset();

		monitor = Policy.monitorFor(monitor);
		Policy.checkCanceled(monitor);

		// add all client files by default
		for (int i = 0; i < clientFiles.length; i++)
		{
			if (!syncContinue(monitor))
				return null;

			Policy.checkCanceled(monitor);

			monitor.worked(1);

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
			if (!syncContinue(monitor))
				return null;

			Policy.checkCanceled(monitor);

			monitor.worked(1);

			IFileStore serverFile = serverFiles[i];
			IFileInfo serverFileInfo = serverFile.fetchInfo(IExtendedFileStore.DETAILED, null);
			String relativePath = getCanonicalPath(_serverFileRoot, serverFile);

			logDebug(FileUtil.NEW_LINE);
			logDebug(MessageFormat.format(Messages.Synchronizer_Comparing_Files, new Object[] { relativePath }));

			if (!fileList.containsKey(relativePath)) // Server only
			{
				if (serverFileInfo.getAttribute(EFS.ATTRIBUTE_SYMLINK))
					continue;

				VirtualFileSyncPair item = new VirtualFileSyncPair(null, serverFile, relativePath,
						SyncState.ServerItemOnly);
				fileList.put(relativePath, item);
				logDebug(Messages.Synchronizer_Item_Not_On_Destination);
				continue;
			}

			// Client and server
			// get client sync item already in our file list
			VirtualFileSyncPair item = fileList.get(relativePath);

			// associate this server file with that sync item
			item.setDestinationFile(serverFile);

			IFileInfo clientFileInfo = item.getSourceFileInfo(monitor);
			if (clientFileInfo == null && item.getSyncState() == SyncState.ServerItemOnly)
			{
				// This is an item we've seen already. Continue on.
				continue;
			}

			if (clientFileInfo.isDirectory() != serverFileInfo.isDirectory())
			{
				// this only occurs if one file is a directory and the other
				// is not a directory
				item.setSyncState(SyncState.IncompatibleFileTypes);
				logDebug(Messages.Synchronizer_Incompatible_Types);
				continue;
			}

			if (serverFileInfo.isDirectory())
			{
				fileList.remove(relativePath);
				logDebug(Messages.Synchronizer_Directory);
				continue;
			}

			// calculate modification time difference, taking server
			// offset into account
			long serverFileTime = serverFileInfo.getLastModified();
			long clientFileTime = clientFileInfo.getLastModified();
			long timeDiff = serverFileTime - clientFileTime;

			logDebug(MessageFormat.format(Messages.Synchronizer_Times_Modified, new long[] { clientFileTime,
					serverFileTime }));

			// check modification date
			if (-this._timeTolerance <= timeDiff && timeDiff <= this._timeTolerance)
			{
				if (this._useCRC && !serverFileInfo.isDirectory())
				{
					item.setSyncState(this.compareCRC(item));
				}
				else
				{
					item.setSyncState(SyncState.ItemsMatch);
					logDebug(Messages.Synchronizer_Items_Identical);
				}
			}
			else
			{
				if (timeDiff < 0)
				{
					item.setSyncState(SyncState.ClientItemIsNewer);
					logDebug(MessageFormat.format(Messages.Synchronizer_Source_Newer,
							new long[] { Math.round(Math.abs(timeDiff / 1000)) }));
				}
				else
				{
					item.setSyncState(SyncState.ServerItemIsNewer);
					logDebug(MessageFormat.format(Messages.Synchronizer_Destination_Newer,
							new long[] { Math.round(Math.abs(timeDiff / 1000)) }));
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
	private int compareCRC(VirtualFileSyncPair item) throws CoreException
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
				IdeLog.logError(SyncingPlugin.getDefault(),
						MessageFormat.format(Messages.Synchronizer_ErrorClosingStreams, item.getRelativePath()), e);
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
			IdeLog.logError(SyncingPlugin.getDefault(), Messages.Synchronizer_ErrorRetrievingCRC, e);

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
	public boolean download(VirtualFileSyncPair[] fileList, IProgressMonitor monitor)
	{
		return downloadAndDelete(fileList, false, monitor);
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
	public boolean downloadAndDelete(VirtualFileSyncPair[] fileList, IProgressMonitor monitor)
	{
		return downloadAndDelete(fileList, true, monitor);
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
	{
		FileWatcher.avoidNotify();
		try
		{
			checkFileManagers();

			logBeginDownloading();

			boolean result = true;
			int totalItems = fileList.length;
			// IConnectionPoint client = getClientFileManager();

			this.reset();

			SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.Synchronizer_Downloading_Files,
					fileList.length);
			Policy.checkCanceled(subMonitor);

			FILE_LOOP: for (int i = 0; i < fileList.length; i++)
			{
				final VirtualFileSyncPair item = fileList[i];
				final IFileStore clientFile = item.getSourceFile();
				final IFileStore serverFile = item.getDestinationFile();

				setSyncItemDirection(item, false, true);

				SubMonitor childMonitor = subMonitor.newChild(1);
				childMonitor.setTaskName(getSyncStatus(item));

				try
				{
					final IFileInfo clientFileInfo = item.getSourceFileInfo();
					final IFileInfo serverFileInfo = item.getDestinationFileInfo();

					// fire event
					if (!syncEvent(item, i, totalItems, childMonitor))
					{
						delete = false;
						break;
					}

					Policy.checkCanceled(childMonitor);

					switch (item.getSyncState())
					{
						case SyncState.ClientItemOnly:
							// only exists on client; checks if it needs to be deleted
							if (delete)
							{
								// Need to query first because deletion makes isDirectory always return false
								boolean wasDirectory = clientFileInfo.isDirectory();
								clientFile.delete(EFS.NONE, null);
								if (wasDirectory)
								{
									this._clientDirectoryDeletedCount++;
								}
								else
								{
									this._clientFileDeletedCount++;
								}
							}
							syncDone(item, childMonitor);
							break;

						case SyncState.ServerItemOnly:
							IFileStore targetClientFile = EFSUtils.createFile(_serverFileRoot,
									item.getDestinationFile(), _clientFileRoot);
							boolean exists = targetClientFile.fetchInfo().exists();
							if (serverFileInfo.isDirectory())
							{
								logCreatedDirectory(targetClientFile);

								if (!exists)
								{
									targetClientFile.mkdir(EFS.NONE, null);
									this._clientDirectoryCreatedCount++;
									_newFilesDownloaded.add(targetClientFile);
									// update permissions for the newly created directory
									updatePermissions(serverFile, targetClientFile, false,
											PermissionDirection.DOWNLOAD, childMonitor);
								}

								logSuccess();
								syncDone(item, childMonitor);
							}
							else
							{
								logDownloading(serverFile);
								try
								{
									SyncUtils
											.copy(serverFile, serverFileInfo, targetClientFile, EFS.NONE, childMonitor);
									Synchronizer.this._serverFileTransferedCount++;
									_newFilesDownloaded.add(targetClientFile);
									// update permissions for the newly created file
									if (!exists)
									{
										updatePermissions(serverFile, targetClientFile, true,
												PermissionDirection.DOWNLOAD, childMonitor);
									}
									logSuccess();
									syncDone(item, childMonitor);
								}
								catch (CoreException e)
								{
									logError(e);
									if (!syncError(item, e, childMonitor))
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
							if (serverFileInfo.isDirectory())
							{
								try
								{
									EFSUtils.setModificationTime(serverFileInfo.getLastModified(), clientFile);
								}
								catch (CoreException e)
								{
									logError(e);
								}

								logSuccess();
								syncDone(item, childMonitor);
							}
							else
							{
								try
								{
									SyncUtils.copy(serverFile, serverFileInfo, clientFile, EFS.NONE, childMonitor);
									Synchronizer.this._serverFileTransferedCount++;
									logSuccess();
									syncDone(item, childMonitor);
								}
								catch (CoreException e)
								{
									logError(e);
									if (!syncError(item, e, childMonitor))
									{
										result = false;
										break FILE_LOOP;
									}
								}
							}
							break;

						default:
							syncDone(item, childMonitor);
							break;
					}
				}
				catch (Exception ex)
				{
					IdeLog.logError(SyncingPlugin.getDefault(), Messages.Synchronizer_ErrorDuringSync, ex);
					result = false;

					if (!syncError(item, ex, childMonitor))
					{
						break FILE_LOOP;
					}
				}
			}

			return result;
		}
		finally
		{
			FileWatcher.resumeNotify();
		}
	}

	/**
	 * Returns a string describing what's going on during the synchronization
	 * 
	 * @param item
	 * @return
	 */
	private String getSyncStatus(VirtualFileSyncPair item)
	{
		if (item.getSyncDirection() == VirtualFileSyncPair.Direction_ClientToServer)
		{
			return MessageFormat.format(Messages.Synchronizer_Uploading, item.getRelativePath());
		}
		if (item.getSyncDirection() == VirtualFileSyncPair.Direction_ServerToClient)
		{
			return MessageFormat.format(Messages.Synchronizer_Downloading, item.getRelativePath());
		}
		return MessageFormat.format(Messages.Synchronizer_Skipping_File, item.getRelativePath());
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
		FileWatcher.avoidNotify();
		try
		{
			logBeginFullSyncing();

			// assume we'll be successful
			boolean result = true;
			int totalItems = fileList.length;

			// reset stats
			this.reset();

			SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.Synchronizer_Synchronizing, fileList.length);
			Policy.checkCanceled(subMonitor);

			// process all items in our list
			FILE_LOOP: for (int i = 0; i < fileList.length; i++)
			{
				final VirtualFileSyncPair item = fileList[i];
				final IFileStore clientFile = item.getSourceFile();
				final IFileStore serverFile = item.getDestinationFile();

				setSyncItemDirection(item, false, true);

				SubMonitor childMonitor = subMonitor.newChild(1);
				childMonitor.setTaskName(getSyncStatus(item));

				try
				{
					final IFileInfo clientFileInfo = item.getSourceFileInfo(childMonitor);
					final IFileInfo serverFileInfo = item.getDestinationFileInfo(childMonitor);

					// fire event
					if (!syncEvent(item, i, totalItems, childMonitor))
					{
						result = false;
						break FILE_LOOP;
					}

					Policy.checkCanceled(childMonitor);

					switch (item.getSyncState())
					{
						case SyncState.ClientItemIsNewer:
							// item exists on both ends, but the client one is newer
							logUploading(serverFile);
							if (clientFileInfo.isDirectory())
							{
								EFSUtils.setModificationTime(clientFileInfo.getLastModified(), serverFile);
								logSuccess();
								syncDone(item, childMonitor);
							}
							else
							{
								try
								{
									SyncUtils.copy(clientFile, clientFileInfo, serverFile, EFS.NONE, childMonitor);
									Synchronizer.this._clientFileTransferedCount++;
									logSuccess();
									syncDone(item, childMonitor);
								}
								catch (CoreException e)
								{
									logError(e);

									if (!syncError(item, e, childMonitor))
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
								boolean wasDirectory = clientFileInfo.isDirectory();
								// deletes the item
								clientFile.delete(EFS.NONE, null);
								if (wasDirectory)
								{
									this._clientDirectoryDeletedCount++;
								}
								else
								{
									this._clientFileDeletedCount++;
								}
								logSuccess();
								syncDone(item, childMonitor);
							}
							else
							{
								// creates the item on server
								IFileStore targetServerFile = EFSUtils.createFile(_clientFileRoot,
										item.getSourceFile(), _serverFileRoot);
								boolean exists = targetServerFile.fetchInfo().exists();
								if (clientFileInfo.isDirectory())
								{
									logCreatedDirectory(targetServerFile);

									if (!exists)
									{
										targetServerFile.mkdir(EFS.NONE, null);
										this._serverDirectoryCreatedCount++;
										_newFilesUploaded.add(targetServerFile);
										// update permissions for the newly created directory
										updatePermissions(clientFile, targetServerFile, false,
												PermissionDirection.UPLOAD, childMonitor);
									}

									logSuccess();
									syncDone(item, childMonitor);
								}
								else
								{
									logUploading(clientFile);
									try
									{
										SyncUtils.copy(clientFile, clientFileInfo, targetServerFile, EFS.NONE,
												childMonitor);
										Synchronizer.this._clientFileTransferedCount++;
										_newFilesUploaded.add(targetServerFile);
										// update permissions for the newly created file
										if (!exists)
										{
											updatePermissions(clientFile, targetServerFile, true,
													PermissionDirection.UPLOAD, childMonitor);
										}
										logSuccess();
										syncDone(item, childMonitor);
									}
									catch (CoreException e)
									{
										logError(e);

										if (!syncError(item, e, childMonitor))
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
							if (serverFileInfo.isDirectory())
							{
								// just needs to set the modification time for directory
								EFSUtils.setModificationTime(serverFileInfo.getLastModified(), clientFile);

								logSuccess();
								syncDone(item, childMonitor);
							}
							else
							{
								try
								{
									SyncUtils.copy(serverFile, serverFileInfo, clientFile, EFS.NONE, childMonitor);
									Synchronizer.this._serverFileTransferedCount++;
									logSuccess();
									syncDone(item, childMonitor);
								}
								catch (CoreException e)
								{
									logError(e);

									if (!syncError(item, e, childMonitor))
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
								boolean wasDirectory = serverFileInfo.isDirectory();
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
								syncDone(item, childMonitor);
							}
							else
							{
								// creates the item on client
								IFileStore targetClientFile = EFSUtils.createFile(_serverFileRoot,
										item.getDestinationFile(), _clientFileRoot);
								boolean exists = targetClientFile.fetchInfo().exists();
								if (serverFileInfo.isDirectory())
								{
									logCreatedDirectory(targetClientFile);

									if (!exists)
									{
										targetClientFile.mkdir(EFS.NONE, null);
										this._clientDirectoryCreatedCount++;
										_newFilesDownloaded.add(targetClientFile);
										// update permissions for the newly created directory
										updatePermissions(serverFile, targetClientFile, false,
												PermissionDirection.DOWNLOAD, childMonitor);
									}

									logSuccess();
									syncDone(item, childMonitor);
								}
								else
								{
									logDownloading(targetClientFile);

									try
									{
										SyncUtils.copy(serverFile, serverFileInfo, targetClientFile, EFS.NONE,
												childMonitor);
										Synchronizer.this._serverFileTransferedCount++;
										_newFilesDownloaded.add(targetClientFile);
										// update permissions for the newly created file
										if (!exists)
										{
											updatePermissions(serverFile, targetClientFile, true,
													PermissionDirection.DOWNLOAD, childMonitor);
										}
										logSuccess();
										syncDone(item, childMonitor);
									}
									catch (CoreException e)
									{
										logError(e);

										if (!syncError(item, e, childMonitor))
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
							IdeLog.logError(
									SyncingPlugin.getDefault(),
									MessageFormat.format(Messages.Synchronizer_FullSyncCRCMismatches,
											item.getRelativePath()), (Throwable) null);
							if (!syncError(item, null, childMonitor))
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
					IdeLog.logError(SyncingPlugin.getDefault(), Messages.Synchronizer_ErrorDuringSync, ex);
					result = false;

					if (!syncError(item, ex, childMonitor))
					{
						break FILE_LOOP;
					}
				}
			}

			return result;
		}
		finally
		{
			FileWatcher.resumeNotify();
		}
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
	public boolean upload(VirtualFileSyncPair[] fileList, IProgressMonitor monitor)
	{
		return uploadAndDelete(fileList, false, monitor);
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
	public boolean uploadAndDelete(VirtualFileSyncPair[] fileList, IProgressMonitor monitor)
	{
		return uploadAndDelete(fileList, true, monitor);
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
	{
		FileWatcher.avoidNotify();
		try
		{
			checkFileManagers();
			logBeginUploading();

			// IConnectionPoint server = getServerFileManager();
			boolean result = true;
			int totalItems = fileList.length;

			this.reset();

			SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.Synchronizer_Uploading_Files, fileList.length);
			Policy.checkCanceled(subMonitor);

			FILE_LOOP: for (int i = 0; i < fileList.length; i++)
			{
				final VirtualFileSyncPair item = fileList[i];
				final IFileStore clientFile = item.getSourceFile();
				final IFileStore serverFile = item.getDestinationFile();

				setSyncItemDirection(item, false, true);

				SubMonitor childMonitor = subMonitor.newChild(1);
				childMonitor.setTaskName(getSyncStatus(item));

				try
				{
					final IFileInfo clientFileInfo = item.getSourceFileInfo(childMonitor);
					final IFileInfo serverFileInfo = item.getDestinationFileInfo(childMonitor);

					// fire event
					if (!syncEvent(item, i, totalItems, childMonitor))
					{
						result = false;
						break;
					}

					Policy.checkCanceled(childMonitor);

					switch (item.getSyncState())
					{
						case SyncState.ClientItemOnly:
							// only exists on client; creates the item on server
							IFileStore targetServerFile = EFSUtils.createFile(_clientFileRoot, item.getSourceFile(),
									_serverFileRoot);
							boolean exists = targetServerFile.fetchInfo().exists();
							if (clientFileInfo.isDirectory())
							{
								if (!exists)
								{
									targetServerFile.mkdir(EFS.NONE, null);
									this._serverDirectoryCreatedCount++;
									_newFilesUploaded.add(targetServerFile);
									// update permissions for the newly created directory
									updatePermissions(clientFile, targetServerFile, false, PermissionDirection.UPLOAD,
											childMonitor);
								}

								syncDone(item, childMonitor);
							}
							else
							{
								logUploading(clientFile);

								try
								{
									SyncUtils
											.copy(clientFile, clientFileInfo, targetServerFile, EFS.NONE, childMonitor);
									Synchronizer.this._clientFileTransferedCount++;
									_newFilesUploaded.add(targetServerFile);
									// update permissions for the newly created file
									if (!exists)
									{
										updatePermissions(clientFile, targetServerFile, true,
												PermissionDirection.UPLOAD, childMonitor);
									}
									logSuccess();
									syncDone(item, childMonitor);
								}
								catch (CoreException e)
								{
									logError(e);

									if (!syncError(item, e, childMonitor))
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
								// Need to query if directory first because deletion makes isDirectory always return
								// false.
								boolean wasDirectory = serverFileInfo.isDirectory();
								serverFile.delete(EFS.NONE, childMonitor);
								if (wasDirectory)
								{
									this._serverDirectoryDeletedCount++;
								}
								else
								{
									this._serverFileDeletedCount++;
								}
							}
							syncDone(item, childMonitor);
							break;

						case SyncState.ClientItemIsNewer:
						case SyncState.CRCMismatch:
							// exists on both sides, but the client item is newer
							logUploading(clientFile);
							if (clientFileInfo.isDirectory())
							{
								// just needs to set the modification time for directory
								try
								{
									EFSUtils.setModificationTime(clientFileInfo.getLastModified(), serverFile);
								}
								catch (CoreException e)
								{
									logError(e);

									if (!syncError(item, e, childMonitor))
									{
										result = false;
										break FILE_LOOP;
									}
								}

								logSuccess();
								syncDone(item, childMonitor);
							}
							else
							{
								try
								{
									SyncUtils.copy(clientFile, clientFileInfo, serverFile, EFS.NONE, childMonitor);
									Synchronizer.this._clientFileTransferedCount++;
									logSuccess();
									syncDone(item, childMonitor);
								}
								catch (CoreException e)
								{
									logError(e);

									if (!syncError(item, e, childMonitor))
									{
										result = false;
										break FILE_LOOP;
									}
								}

							}
							break;

						default:
							syncDone(item, childMonitor);
							break;
					}
				}
				catch (Exception ex)
				{
					IdeLog.logError(SyncingPlugin.getDefault(), Messages.Synchronizer_ErrorDuringSync, ex);
					result = false;

					if (!syncError(item, ex, childMonitor))
					{
						break FILE_LOOP;
					}
				}
			}

			return result;
		}
		finally
		{
			FileWatcher.resumeNotify();
		}
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

	private static void updatePermissions(IFileStore sourceFileStore, IFileStore targetFileStore, boolean isFile,
			PermissionDirection direction, IProgressMonitor monitor)
	{
		if (PreferenceUtils.getUpdatePermissions(direction))
		{
			IFileInfo targetFileInfo = getFileInfo(targetFileStore, monitor);
			long permissions = 0;
			if (PreferenceUtils.getSpecificPermissions(direction))
			{
				// use specified permissions from preferences
				permissions = isFile ? PreferenceUtils.getFilePermissions(direction) : PreferenceUtils
						.getFolderPermissions(direction);
			}
			else
			{
				// uses source's permissions
				IFileInfo sourceFileInfo = getFileInfo(sourceFileStore, monitor);
				if (sourceFileInfo != null)
				{
					permissions = getPermissions(sourceFileInfo);
				}
			}
			if (permissions > 0)
			{
				if (targetFileInfo instanceof IExtendedFileInfo)
				{
					((IExtendedFileInfo) targetFileInfo).setPermissions(permissions);
				}
				else
				{
					targetFileInfo.setAttribute(EFS.ATTRIBUTE_OWNER_READ,
							(permissions & IExtendedFileInfo.PERMISSION_OWNER_READ) != 0);
					targetFileInfo.setAttribute(EFS.ATTRIBUTE_OWNER_WRITE,
							(permissions & IExtendedFileInfo.PERMISSION_OWNER_WRITE) != 0);
					targetFileInfo.setAttribute(EFS.ATTRIBUTE_OWNER_EXECUTE,
							(permissions & IExtendedFileInfo.PERMISSION_OWNER_EXECUTE) != 0);
					targetFileInfo.setAttribute(EFS.ATTRIBUTE_GROUP_READ,
							(permissions & IExtendedFileInfo.PERMISSION_GROUP_READ) != 0);
					targetFileInfo.setAttribute(EFS.ATTRIBUTE_GROUP_WRITE,
							(permissions & IExtendedFileInfo.PERMISSION_GROUP_WRITE) != 0);
					targetFileInfo.setAttribute(EFS.ATTRIBUTE_GROUP_EXECUTE,
							(permissions & IExtendedFileInfo.PERMISSION_GROUP_EXECUTE) != 0);
					targetFileInfo.setAttribute(EFS.ATTRIBUTE_OTHER_READ,
							(permissions & IExtendedFileInfo.PERMISSION_OTHERS_READ) != 0);
					targetFileInfo.setAttribute(EFS.ATTRIBUTE_OTHER_WRITE,
							(permissions & IExtendedFileInfo.PERMISSION_OTHERS_WRITE) != 0);
					targetFileInfo.setAttribute(EFS.ATTRIBUTE_OTHER_EXECUTE,
							(permissions & IExtendedFileInfo.PERMISSION_OTHERS_EXECUTE) != 0);
				}
			}
			try
			{
				if (targetFileInfo instanceof IExtendedFileInfo)
				{
					targetFileStore.putInfo(targetFileInfo, IExtendedFileInfo.SET_PERMISSIONS, monitor);
				}
				else
				{
					targetFileStore.putInfo(targetFileInfo, EFS.SET_ATTRIBUTES, monitor);
				}
			}
			catch (CoreException e)
			{
				IdeLog.logWarning(SyncingPlugin.getDefault(), "Failed to update permissions for " + targetFileStore, e); //$NON-NLS-1$
			}
		}
	}

	private static IFileInfo getFileInfo(IFileStore fileStore, IProgressMonitor monitor)
	{
		IFileInfo fileInfo = (IFileInfo) fileStore.getAdapter(IFileInfo.class);
		if (fileInfo != null)
		{
			return fileInfo;
		}
		try
		{
			return fileStore.fetchInfo(EFS.NONE, monitor);
		}
		catch (CoreException e)
		{
			// ignores the exception
		}
		return null;
	}

	private static long getPermissions(IFileInfo fileInfo)
	{
		if (fileInfo instanceof IExtendedFileInfo)
		{
			return ((IExtendedFileInfo) fileInfo).getPermissions();
		}
		long permissions = 0;
		permissions |= fileInfo.getAttribute(EFS.ATTRIBUTE_OWNER_READ) ? IExtendedFileInfo.PERMISSION_OWNER_READ : 0;
		permissions |= fileInfo.getAttribute(EFS.ATTRIBUTE_OWNER_WRITE) ? IExtendedFileInfo.PERMISSION_OWNER_WRITE : 0;
		permissions |= fileInfo.getAttribute(EFS.ATTRIBUTE_OWNER_EXECUTE) ? IExtendedFileInfo.PERMISSION_OWNER_EXECUTE
				: 0;
		permissions |= fileInfo.getAttribute(EFS.ATTRIBUTE_GROUP_READ) ? IExtendedFileInfo.PERMISSION_GROUP_READ : 0;
		permissions |= fileInfo.getAttribute(EFS.ATTRIBUTE_GROUP_WRITE) ? IExtendedFileInfo.PERMISSION_GROUP_WRITE : 0;
		permissions |= fileInfo.getAttribute(EFS.ATTRIBUTE_GROUP_EXECUTE) ? IExtendedFileInfo.PERMISSION_GROUP_EXECUTE
				: 0;
		permissions |= fileInfo.getAttribute(EFS.ATTRIBUTE_OTHER_READ) ? IExtendedFileInfo.PERMISSION_OTHERS_READ : 0;
		permissions |= fileInfo.getAttribute(EFS.ATTRIBUTE_OTHER_WRITE) ? IExtendedFileInfo.PERMISSION_OTHERS_WRITE : 0;
		permissions |= fileInfo.getAttribute(EFS.ATTRIBUTE_OTHER_EXECUTE) ? IExtendedFileInfo.PERMISSION_OTHERS_EXECUTE
				: 0;
		return permissions;
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
	 * @see com.com.aptana.ide.syncing.core.old.ILoggable#getLogger()
	 */
	public ILogger getLogger()
	{
		return this.logger;
	}

	/**
	 * @see com.com.aptana.ide.syncing.core.old.ILoggable#setLogger(com.com.aptana.ide.syncing.core.old.ILogger)
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
				+ MessageFormat.format(Messages.Synchronizer_BeginningDownload, getTimestamp()));
	}

	private void logBeginFullSyncing()
	{
		log(FileUtil.NEW_LINE + FileUtil.NEW_LINE
				+ MessageFormat.format(Messages.Synchronizer_BeginningFullSync, getTimestamp()));
	}

	private void logBeginUploading()
	{
		log(FileUtil.NEW_LINE + FileUtil.NEW_LINE
				+ MessageFormat.format(Messages.Synchronizer_BeginningUpload, getTimestamp()));
	}

	private void logCreatedDirectory(IFileStore file)
	{
		log(FileUtil.NEW_LINE
				+ MessageFormat.format(Messages.Synchronizer_CreatedDirectory, EFSUtils.getAbsolutePath(file)));
	}

	private void logDownloading(IFileStore file)
	{
		log(FileUtil.NEW_LINE + MessageFormat.format(Messages.Synchronizer_Downloading, EFSUtils.getAbsolutePath(file)));
	}

	private void logDebug(String message)
	{
		// log(message);
	}

	private void logError(Exception e)
	{
		IdeLog.logError(SyncingPlugin.getDefault(), e);
		if (this.logger != null)
		{
			if (e.getCause() != null)
			{
				log(MessageFormat.format(Messages.Synchronizer_Error_Extended, new Object[] { e.getLocalizedMessage(),
						e.getCause().getLocalizedMessage() }));
			}
			else
			{
				log(MessageFormat.format(Messages.Synchronizer_Error, e.getLocalizedMessage()));
			}
		}
	}

	private void logSuccess()
	{
		log(Messages.Synchronizer_Success);
	}

	private void logUploading(IFileStore file)
	{
		log(FileUtil.NEW_LINE + MessageFormat.format(Messages.Synchronizer_Uploading, EFSUtils.getAbsolutePath(file)));
	}

	private void syncDone(VirtualFileSyncPair item, IProgressMonitor monitor)
	{
		if (this._eventHandler != null)
		{
			this._eventHandler.syncDone(item, monitor);
		}

		if (monitor != null)
		{
			monitor.worked(1);
		}
	}

	private boolean syncError(VirtualFileSyncPair item, Exception e, IProgressMonitor monitor)
	{
		return this._eventHandler == null || this._eventHandler.syncErrorEvent(item, e, monitor);
	}

	private boolean syncEvent(VirtualFileSyncPair item, int index, int totalItems, IProgressMonitor monitor)
	{
		return this._eventHandler == null || this._eventHandler.syncEvent(item, index, totalItems, monitor);
	}

	private boolean syncContinue(IProgressMonitor monitor)
	{
		return this._eventHandler == null || this._eventHandler.syncContinue(monitor);
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
			IdeLog.logError(SyncingPlugin.getDefault(), e);
		}
	}

}
