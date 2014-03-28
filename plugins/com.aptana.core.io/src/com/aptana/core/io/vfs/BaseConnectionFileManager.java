/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeWhereCreated
// $codepro.audit.disable declaredExceptions
// $codepro.audit.disable variableDeclaredInLoop
// $codepro.audit.disable questionableAssignment
// $codepro.audit.disable exceptionUsage.exceptionCreation

package com.aptana.core.io.vfs;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ProgressMonitorInterrupter;
import com.aptana.core.util.ProgressMonitorInterrupter.InterruptDelegate;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.InfiniteProgressMonitor;
import com.aptana.ide.core.io.PermissionDeniedException;
import com.aptana.ide.core.io.preferences.PermissionDirection;
import com.aptana.ide.core.io.preferences.PreferenceUtils;

/**
 * @author Max Stepanov
 */
public abstract class BaseConnectionFileManager implements IConnectionFileManager
{

	protected static final int CACHE_TTL = 60000; /* 1min */

	private static final int RETRIES_AFTER_FAILURE = 2;
	protected static final char[] EMPTY_PASSWORD = StringUtil.EMPTY.toCharArray();
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private static final byte[] EMPTY_BYTES = new byte[0];
	private static final IExtendedFileInfo[] EMPTY_FILEINFO_ARRAY = new IExtendedFileInfo[0];

	protected String login;
	protected char[] password = EMPTY_PASSWORD;
	protected IPath basePath;
	protected String authId;

	private Map<IPath, ExtendedFileInfo> fileInfoCache;
	private Map<IPath, ExtendedFileInfo[]> fileInfosCache;

	private final InterruptDelegate interruptDelegate = new InterruptDelegate()
	{
		public void interrupt()
		{
			interruptOperation();
		}
	};

	protected final void promptPassword(String title, String message)
	{
		password = CoreIOPlugin.getAuthenticationManager().promptPassword(authId, login, title, message);
		if (password == null)
		{
			password = EMPTY_PASSWORD;
			throw new OperationCanceledException();
		}
	}

	protected final void getOrPromptPassword(String title, String message)
	{
		password = CoreIOPlugin.getAuthenticationManager().getPassword(authId);
		if (password == null)
		{
			password = EMPTY_PASSWORD;
			promptPassword(title, message);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#fetchInfo(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized IExtendedFileInfo fetchInfo(IPath path, int options, IProgressMonitor monitor)
			throws CoreException
	{
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(
				MessageFormat.format(Messages.BaseConnectionFileManager_gethering_details, path.toPortableString()), 2);
		try
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(interruptDelegate);
			ExtendedFileInfo fileInfo = getCachedFileInfo(path);
			if (fileInfo == null)
			{
				testOrConnect(monitor);
				fileInfo = fetchAndCacheFileInfo(path, options, monitor);
				setLastOperationTime();
			}
			return (IExtendedFileInfo) fileInfo.clone();
		}
		finally
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#childNames(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized String[] childNames(IPath path, int options, IProgressMonitor monitor)
			throws CoreException
	{
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(
				MessageFormat.format(Messages.BaseConnectionFileManager_listing_directory, path.toPortableString()), 2);
		try
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(interruptDelegate);
			ExtendedFileInfo[] fileInfos = getCachedFileInfos(path);
			if (fileInfos != null)
			{
				List<String> list = new ArrayList<String>();
				for (ExtendedFileInfo fileInfo : fileInfos)
				{
					list.add(fileInfo.getName());
				}
				return list.toArray(new String[list.size()]);
			}
			testOrConnect(monitor);
			String[] result = listDirectory(basePath.append(path), monitor);
			setLastOperationTime();
			return result;
		}
		catch (FileNotFoundException e)
		{
			setLastOperationTime();
			return EMPTY_STRING_ARRAY;
		}
		finally
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#childInfos(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized IExtendedFileInfo[] childInfos(IPath path, int options, IProgressMonitor monitor)
			throws CoreException
	{
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(
				MessageFormat.format(Messages.BaseConnectionFileManager_gethering_details, path.toPortableString()), 2);
		options = (options & IExtendedFileStore.DETAILED);
		try
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(interruptDelegate);
			ExtendedFileInfo[] fileInfos = getCachedFileInfos(path);
			if (fileInfos == null)
			{
				testOrConnect(monitor);
				try
				{
					fileInfos = cache(path, fetchFilesInternal(basePath.append(path), options, monitor));
					for (ExtendedFileInfo fileInfo : fileInfos)
					{
						postProcessFileInfo(fileInfo, basePath.append(path), options, monitor);
						cache(path.append(fileInfo.getName()), fileInfo);
					}
					setLastOperationTime();
				}
				catch (FileNotFoundException e)
				{
					setLastOperationTime();
					return EMPTY_FILEINFO_ARRAY;
				}
				catch (PermissionDeniedException e)
				{
					setLastOperationTime();
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID, MessageFormat.format(
							Messages.BaseConnectionFileManager_PermissionDenied0, path.toPortableString()), e));
				}
			}
			return fileInfos.clone();
		}
		finally
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#openInputStream(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized InputStream openInputStream(IPath path, int options, IProgressMonitor monitor)
			throws CoreException
	{
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(
				MessageFormat.format(Messages.BaseConnectionFileManager_opening_file, path.toPortableString()), 3);
		try
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(interruptDelegate);
			testOrConnect(monitor);
			ExtendedFileInfo fileInfo = fetchAndCacheFileInfo(path, Policy.subMonitorFor(monitor, 1));
			setLastOperationTime();
			if (!fileInfo.exists())
			{
				throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
						Messages.BaseConnectionFileManager_no_such_file, initFileNotFoundException(path, null)));
			}
			if (fileInfo.isDirectory())
			{
				throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
						Messages.BaseConnectionFileManager_file_is_directory, initFileNotFoundException(path, null)));
			}
			if (fileInfo.getLength() == 0)
			{
				return new ByteArrayInputStream(EMPTY_BYTES);
			}
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			return readFile(basePath.append(path), Policy.subMonitorFor(monitor, 1));
		}
		catch (FileNotFoundException e)
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			setLastOperationTime();
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.BaseConnectionFileManager_no_such_file, initFileNotFoundException(path, e.getCause())));
		}
		catch (CoreException e)
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			throw e;
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#openOutputStream(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized OutputStream openOutputStream(IPath path, int options, IProgressMonitor monitor)
			throws CoreException
	{
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(
				MessageFormat.format(Messages.BaseConnectionFileManager_opening_file, path.toPortableString()), 3);
		try
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(interruptDelegate);
			testOrConnect(monitor);
			ExtendedFileInfo fileInfo = fetchAndCacheFileInfo(path, Policy.subMonitorFor(monitor, 1));
			setLastOperationTime();
			if (fileInfo.exists() && fileInfo.isDirectory())
			{
				throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
						Messages.BaseConnectionFileManager_file_is_directory, initFileNotFoundException(path, null)));
			}
			long permissions = -1;
			boolean useTemporary = canUseTemporaryFile(path, fileInfo, Policy.subMonitorFor(monitor, 1));
			if (fileInfo.exists())
			{
				if (useTemporary)
				{
					permissions = fileInfo.getPermissions();
				}
			}
			else
			{
				// new file; check if to use the user-defined default permissions
				if (PreferenceUtils.getUpdatePermissions(PermissionDirection.UPLOAD)
						&& PreferenceUtils.getSpecificPermissions(PermissionDirection.UPLOAD))
				{
					permissions = PreferenceUtils.getFilePermissions(PermissionDirection.UPLOAD);
				}
			}
			clearCache(path);
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			return writeFile(basePath.append(path), useTemporary, permissions, Policy.subMonitorFor(monitor, 1));
		}
		catch (FileNotFoundException e)
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			setLastOperationTime();
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.BaseConnectionFileManager_parent_doesnt_exist, initFileNotFoundException(path,
							e.getCause())));
		}
		catch (CoreException e)
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			throw e;
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#delete(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized void delete(IPath path, int options, IProgressMonitor monitor) throws CoreException
	{
		monitor = Policy.monitorFor(monitor);
		monitor = new InfiniteProgressMonitor(monitor);
		monitor.beginTask(Messages.BaseConnectionFileManager_deleting, 20);
		try
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(interruptDelegate);
			testOrConnect(monitor);
			ExtendedFileInfo fileInfo = getCachedFileInfo(path);
			if (fileInfo == null)
			{
				fileInfo = fetchAndCacheFileInfo(path, IExtendedFileStore.EXISTENCE, Policy.subMonitorFor(monitor, 1));
			}
			if (!fileInfo.exists())
			{
				return;
			}
			Policy.checkCanceled(monitor);
			try
			{
				if (fileInfo.isDirectory())
				{
					deleteDirectory(basePath.append(path), monitor);
				}
				else
				{
					deleteFile(basePath.append(path), monitor);
				}
				setLastOperationTime();
			}
			catch (FileNotFoundException ignore)
			{
				setLastOperationTime();
			}
			finally
			{
				clearCache(path);
			}
		}
		finally
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#mkdir(org.eclipse.core.runtime.IPath, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized void mkdir(IPath path, int options, IProgressMonitor monitor) throws CoreException
	{
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(
				MessageFormat.format(Messages.BaseConnectionFileManager_creating_folder, path.toPortableString()), 3);
		try
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(interruptDelegate);
			testOrConnect(monitor);
			ExtendedFileInfo fileInfo = fetchAndCacheFileInfo(path, IExtendedFileStore.EXISTENCE,
					Policy.subMonitorFor(monitor, 1));
			setLastOperationTime();
			if (fileInfo.exists())
			{
				if (!fileInfo.isDirectory())
				{
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_file_already_exists, initFileNotFoundException(path,
									null)));
				}
				return;
			}
			if ((options & EFS.SHALLOW) != 0 && path.segmentCount() > 1)
			{
				fileInfo = fetchAndCacheFileInfo(path.removeLastSegments(1), IExtendedFileStore.EXISTENCE,
						Policy.subMonitorFor(monitor, 1));
				setLastOperationTime();
				if (!fileInfo.exists())
				{
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_parent_doesnt_exist, initFileNotFoundException(path,
									null)));
				}
				if (!fileInfo.isDirectory())
				{
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_parent_is_not_directory, initFileNotFoundException(path,
									null)));
				}
				createDirectory(basePath.append(path), Policy.subMonitorFor(monitor, 1));
			}
			else if (path.segmentCount() == 1)
			{
				createDirectory(basePath.append(path), Policy.subMonitorFor(monitor, 1));
			}
			else
			{
				IProgressMonitor subMonitor = Policy.subMonitorFor(monitor, 1);
				subMonitor.beginTask(Messages.BaseConnectionFileManager_creating_folders, path.segmentCount());
				for (int i = path.segmentCount() - 1; i >= 0; --i)
				{
					createDirectory(basePath.append(path).removeLastSegments(i), subMonitor);
					subMonitor.worked(1);
				}
				subMonitor.done();
			}
			setLastOperationTime();
		}
		catch (FileNotFoundException e)
		{
			setLastOperationTime();
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.BaseConnectionFileManager_parent_doesnt_exist, initFileNotFoundException(path, e)
							.getCause()));
		}
		finally
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#putInfo(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.filesystem.IFileInfo, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized void putInfo(IPath path, IFileInfo info, int options, IProgressMonitor monitor)
			throws CoreException
	{
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(
				MessageFormat.format(Messages.BaseConnectionFileManager_putting_changes, path.toPortableString()), 5);
		try
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(interruptDelegate);
			testOrConnect(monitor);
			if ((options & EFS.SET_LAST_MODIFIED) != 0)
			{
				setModificationTime(basePath.append(path), info.getLastModified(), Policy.subMonitorFor(monitor, 1));
				setLastOperationTime();
			}
			if ((options & EFS.SET_ATTRIBUTES) != 0 && (options & IExtendedFileInfo.SET_PERMISSIONS) == 0)
			{
				ExtendedFileInfo fileInfo = fetchAndCacheFileInfo(path, Policy.subMonitorFor(monitor, 1));
				if (fileInfo.exists())
				{
					long permissions = fileInfo.getPermissions();
					if (!info.getAttribute(EFS.ATTRIBUTE_READ_ONLY))
					{
						permissions |= IExtendedFileInfo.PERMISSION_OWNER_WRITE;
					}
					else
					{
						permissions &= ~IExtendedFileInfo.PERMISSION_OWNER_WRITE;
					}
					if (info.getAttribute(EFS.ATTRIBUTE_EXECUTABLE))
					{
						permissions |= IExtendedFileInfo.PERMISSION_OWNER_EXECUTE;
					}
					else
					{
						permissions &= ~IExtendedFileInfo.PERMISSION_OWNER_EXECUTE;
					}
					changeFilePermissions(basePath.append(path), permissions, Policy.subMonitorFor(monitor, 1));
				}
				setLastOperationTime();
			}
			if (info instanceof IExtendedFileInfo)
			{
				IExtendedFileInfo extInfo = (IExtendedFileInfo) info;
				if ((options & IExtendedFileInfo.SET_PERMISSIONS) != 0)
				{
					changeFilePermissions(basePath.append(path), extInfo.getPermissions(),
							Policy.subMonitorFor(monitor, 1));
				}
				if ((options & IExtendedFileInfo.SET_GROUP) != 0)
				{
					changeFileGroup(basePath.append(path), extInfo.getGroup(), Policy.subMonitorFor(monitor, 1));
				}
				setLastOperationTime();
			}
		}
		catch (FileNotFoundException e)
		{
			setLastOperationTime();
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.BaseConnectionFileManager_no_such_file, initFileNotFoundException(path, e.getCause())));
		}
		finally
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			clearCache(path);
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.io.vfs.IConnectionFileManager#move(org.eclipse.core.runtime.IPath,
	 * org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized void move(IPath sourcePath, IPath destinationPath, int options, IProgressMonitor monitor)
			throws CoreException
	{
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(
				MessageFormat.format(Messages.BaseConnectionFileManager_moving, sourcePath.toPortableString()), 5);
		try
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(interruptDelegate);
			testOrConnect(monitor);
			ExtendedFileInfo fileInfo = fetchAndCacheFileInfo(sourcePath, IExtendedFileStore.EXISTENCE,
					Policy.subMonitorFor(monitor, 1));
			setLastOperationTime();
			if (!fileInfo.exists())
			{
				throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
						Messages.BaseConnectionFileManager_no_such_file, initFileNotFoundException(sourcePath, null)));
			}
			boolean isDirectory = fileInfo.isDirectory();
			fileInfo = fetchAndCacheFileInfo(destinationPath, IExtendedFileStore.EXISTENCE,
					Policy.subMonitorFor(monitor, 1));
			setLastOperationTime();
			if (fileInfo.exists())
			{
				if ((options & EFS.OVERWRITE) == 0)
				{
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_file_already_exists, initFileNotFoundException(
									destinationPath, null)));
				}
				if (fileInfo.isDirectory() != isDirectory)
				{
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_cant_move));
				}
			}
			else
			{
				try
				{
					changeCurrentDir(basePath.append(destinationPath).removeLastSegments(1));
				}
				catch (FileNotFoundException e)
				{
					setLastOperationTime();
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_parent_doesnt_exist, initFileNotFoundException(
									destinationPath, e.getCause())));
				}
				catch (Exception e)
				{
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_failed_change_directory, initFileNotFoundException(
									destinationPath, null)));

				}
			}
			clearCache(sourcePath);
			clearCache(destinationPath);
			if (isDirectory)
			{
				renameDirectory(basePath.append(sourcePath), basePath.append(destinationPath),
						Policy.subMonitorFor(monitor, 2));
			}
			else
			{
				renameFile(basePath.append(sourcePath), basePath.append(destinationPath),
						Policy.subMonitorFor(monitor, 2));
			}
			setLastOperationTime();
		}
		catch (FileNotFoundException e)
		{
			setLastOperationTime();
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.BaseConnectionFileManager_no_such_file,
					initFileNotFoundException(sourcePath, e.getCause())));
		}
		finally
		{
			ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
			monitor.done();
		}
	}

	protected abstract void testConnection(boolean force);

	protected abstract boolean canUseTemporaryFile(IPath path, ExtendedFileInfo fileInfo, IProgressMonitor monitor);

	// all methods here accept absolute path
	protected abstract void changeCurrentDir(IPath path) throws Exception, IOException, CoreException;

	protected abstract ExtendedFileInfo fetchFile(IPath path, int options, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException, PermissionDeniedException;

	protected abstract ExtendedFileInfo[] fetchFiles(IPath path, int options, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException, PermissionDeniedException;

	protected abstract String[] listDirectory(IPath path, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException;

	protected abstract InputStream readFile(IPath path, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException;

	protected abstract OutputStream writeFile(IPath path, boolean useTemporary, long permissions,
			IProgressMonitor monitor) throws CoreException, FileNotFoundException;

	protected abstract void createFile(IPath path, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException, PermissionDeniedException;

	protected abstract void createDirectory(IPath path, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException;

	protected abstract void renameFile(IPath sourcePath, IPath destinationPath, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException;

	protected abstract void renameDirectory(IPath sourcePath, IPath destinationPath, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException;

	protected abstract void deleteFile(IPath path, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException;

	protected abstract void deleteDirectory(IPath path, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException;

	protected abstract void setModificationTime(IPath path, long modificationTime, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException;

	protected abstract void changeFilePermissions(IPath path, long permissions, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException;

	protected abstract void changeFileGroup(IPath path, String group, IProgressMonitor monitor) throws CoreException,
			FileNotFoundException;

	private final ExtendedFileInfo[] fetchFilesInternal(IPath path, int options, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException, PermissionDeniedException
	{
		MultiStatus multiStatus = null;
		boolean force = false;
		for (int trial = 0; trial <= RETRIES_AFTER_FAILURE; ++trial)
		{
			try
			{
				testOrConnect(force, Policy.subMonitorFor(monitor, 1));
				return fetchFiles(path, options, monitor);
			}
			catch (CoreException e)
			{
				IStatus status = e.getStatus();
				if (multiStatus == null)
				{
					multiStatus = new MultiStatus(status.getPlugin(), status.getCode(), status.getMessage(), null);
				}
				IStatus[] childStatus = multiStatus.getChildren();
				if (childStatus.length < 1
						|| !childStatus[childStatus.length - 1].getException().getClass()
								.isInstance(status.getException()))
				{
					multiStatus.add(status);
				}
				force = e.getCause() instanceof IOException || trial > 0;
			}
		}
		throw new CoreException(multiStatus);
	}

	protected final ExtendedFileInfo fetchFileInternal(IPath path, int options, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException, PermissionDeniedException
	{
		MultiStatus multiStatus = null;
		boolean force = false;
		for (int trial = 0; trial <= RETRIES_AFTER_FAILURE; ++trial)
		{
			try
			{
				testOrConnect(force, Policy.subMonitorFor(monitor, 1));
				return fetchFile(path, options, monitor);
			}
			catch (CoreException e)
			{
				IStatus status = e.getStatus();
				if (multiStatus == null)
				{
					multiStatus = new MultiStatus(status.getPlugin(), status.getCode(), status.getMessage(), null);
				}
				IStatus[] childStatus = multiStatus.getChildren();
				if (childStatus.length < 1
						|| !childStatus[childStatus.length - 1].getException().getClass()
								.isInstance(status.getException()))
				{
					multiStatus.add(status);
				}
				force = e.getCause() instanceof IOException || trial > 0;
			}
		}
		throw new CoreException(multiStatus);
	}

	private ExtendedFileInfo fetchAndCacheFileInfo(IPath path, IProgressMonitor monitor) throws CoreException
	{
		return fetchAndCacheFileInfo(path, EFS.NONE, monitor);
	}

	private ExtendedFileInfo fetchAndCacheFileInfo(IPath path, int options, IProgressMonitor monitor)
			throws CoreException
	{
		ExtendedFileInfo fileInfo;
		try
		{
			fileInfo = fetchFileInternal(basePath.append(path), options, monitor);
		}
		catch (FileNotFoundException e)
		{
			fileInfo = new ExtendedFileInfo((path.segmentCount() > 0) ? path.lastSegment()
					: Path.ROOT.toPortableString());
			fileInfo.setExists(false);
			return fileInfo;
		}
		catch (PermissionDeniedException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID, MessageFormat.format(
					Messages.BaseConnectionFileManager_PermissionDenied0, basePath.append(path).toPortableString()), e));
		}
		if (path.segmentCount() == 0)
		{
			fileInfo.setName(Path.ROOT.toPortableString());
		}
		postProcessFileInfo(fileInfo, path, options, monitor);
		return cache(path, fileInfo);
	}

	private void postProcessFileInfo(ExtendedFileInfo fileInfo, IPath dirPath, int options, IProgressMonitor monitor)
			throws CoreException
	{
		if (fileInfo.getAttribute(EFS.ATTRIBUTE_SYMLINK))
		{
			try
			{
				ExtendedFileInfo targetFileInfo = resolveSymlink(dirPath,
						fileInfo.getStringAttribute(EFS.ATTRIBUTE_LINK_TARGET), options, monitor);
				fileInfo.setExists(targetFileInfo.exists());
				if (targetFileInfo.exists())
				{
					fileInfo.setDirectory(targetFileInfo.isDirectory());
					fileInfo.setLength(targetFileInfo.getLength());
					fileInfo.setLastModified(targetFileInfo.getLastModified());
					fileInfo.setOwner(targetFileInfo.getOwner());
					fileInfo.setGroup(targetFileInfo.getGroup());
					fileInfo.setPermissions(targetFileInfo.getPermissions());
				}
				else
				{
					throw new FileNotFoundException(fileInfo.getName());
				}
			}
			catch (FileNotFoundException e)
			{
				try
				{
					changeCurrentDir(dirPath.append(fileInfo.getName()));
					fileInfo.setExists(true);
					fileInfo.setDirectory(true);
				}
				catch (FileNotFoundException fnfe)
				{
					fileInfo.setExists(false);
				}
				catch (Exception ignore)
				{
					IdeLog.logWarning(CoreIOPlugin.getDefault(),
							Messages.BaseConnectionFileManager_symlink_resolve_failed, e);
				}
			}
		}
		long permissions = fileInfo.getPermissions();
		fileInfo.setAttribute(EFS.ATTRIBUTE_READ_ONLY, (permissions & IExtendedFileInfo.PERMISSION_OWNER_WRITE) == 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_EXECUTABLE, (permissions & IExtendedFileInfo.PERMISSION_OWNER_EXECUTE) != 0);
	}

	private ExtendedFileInfo resolveSymlink(IPath dirPath, String linkTarget, int options, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException
	{
		Set<IPath> visited = new HashSet<IPath>();
		visited.add(dirPath);
		while (linkTarget != null && linkTarget.length() > 0)
		{
			IPath targetPath = Path.fromPortableString(linkTarget);
			if (!targetPath.isAbsolute())
			{
				targetPath = dirPath.append(targetPath);
			}
			if (visited.contains(targetPath))
			{
				break;
			}
			visited.add(targetPath);
			ExtendedFileInfo targetFileInfo = getCachedFileInfo(targetPath);
			if (targetFileInfo == null)
			{
				Policy.checkCanceled(monitor);
				try
				{
					targetFileInfo = cache(targetPath,
							fetchFileInternal(targetPath, options, Policy.subMonitorFor(monitor, 1)));
				}
				catch (PermissionDeniedException e)
				{
					// permission denied is like file not found for the case of symlink resolving
					throw initFileNotFoundException(targetPath, e);
				}
			}
			cache(targetPath, targetFileInfo);
			if (targetFileInfo.getAttribute(EFS.ATTRIBUTE_SYMLINK))
			{
				linkTarget = targetFileInfo.getStringAttribute(EFS.ATTRIBUTE_LINK_TARGET);
				dirPath = targetPath.removeLastSegments(1);
				continue;
			}
			return targetFileInfo;
		}
		return new ExtendedFileInfo();
	}

	private final ExtendedFileInfo getCachedFileInfo(IPath path)
	{
		return (fileInfoCache != null) ? fileInfoCache.get(path) : null;
	}

	private final ExtendedFileInfo[] getCachedFileInfos(IPath path)
	{
		return (fileInfosCache != null) ? fileInfosCache.get(path) : null;
	}

	private final ExtendedFileInfo cache(IPath path, ExtendedFileInfo fileInfo)
	{
		if (fileInfoCache != null && fileInfo.exists())
		{
			fileInfoCache.put(path, fileInfo);
		}
		return fileInfo;
	}

	private final ExtendedFileInfo[] cache(IPath path, ExtendedFileInfo[] fileInfos)
	{
		if (fileInfosCache != null)
		{
			fileInfosCache.put(path, fileInfos);
		}
		return fileInfos;
	}

	protected void clearCache(IPath path)
	{
		int segments = path.segmentCount();
		if (fileInfoCache != null)
		{
			for (IPath p : new ArrayList<IPath>(fileInfoCache.keySet()))
			{
				if (p.segmentCount() >= segments && path.matchingFirstSegments(p) == segments)
				{
					fileInfoCache.remove(p);
				}
			}
		}
		if (fileInfosCache != null)
		{
			for (IPath p : new ArrayList<IPath>(fileInfosCache.keySet()))
			{
				if (p.segmentCount() >= segments && path.matchingFirstSegments(p) == segments)
				{
					fileInfosCache.remove(p);
				}
			}
		}
	}

	protected final void cleanup()
	{
		if (fileInfoCache != null)
		{
			fileInfoCache.clear();
		}
		if (fileInfosCache != null)
		{
			fileInfosCache.clear();
		}
	}

	protected void interruptOperation()
	{
		Thread.currentThread().interrupt();
	}

	protected void setLastOperationTime()
	{
	}

	protected final void testOrConnect(IProgressMonitor monitor) throws CoreException
	{
		testOrConnect(false, monitor);
	}

	protected final void testOrConnect(boolean force, IProgressMonitor monitor) throws CoreException
	{
		Policy.checkCanceled(monitor);
		testConnection(force);
		if (!isConnected())
		{
			connect(Policy.subMonitorFor(monitor, 1));
			Policy.checkCanceled(monitor);
		}
	}

	protected static FileNotFoundException initFileNotFoundException(IPath path, Throwable cause)
	{
		FileNotFoundException e = new FileNotFoundException(path.toPortableString());
		if (cause != null)
		{
			e.initCause(cause);
		}
		return e;
	}

}
