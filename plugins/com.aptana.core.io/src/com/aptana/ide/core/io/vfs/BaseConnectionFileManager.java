/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io.vfs;

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
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.aptana.core.util.ExpiringMap;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.InfiniteProgressMonitor;
import com.aptana.ide.core.io.PermissionDeniedException;
import com.aptana.ide.core.io.preferences.PreferenceUtils;

/**
 * @author Max Stepanov
 *
 */
public abstract class BaseConnectionFileManager implements IConnectionFileManager {

	protected static final int CACHE_TTL = 60000; /* 1min */

	protected String login;
	protected char[] password = new char[0];
	protected IPath basePath;
	protected String authId;

	private Map<IPath, ExtendedFileInfo> fileInfoCache;
	private Map<IPath, ExtendedFileInfo[]> fileInfosCache;

	protected final void promptPassword(String title, String message) {
		password = CoreIOPlugin.getAuthenticationManager().promptPassword(
						authId, login, title, message);
		if (password == null) {
		    password = new char[0];
			throw new OperationCanceledException();
		}
	}

	protected final void getOrPromptPassword(String title, String message) {
		password = CoreIOPlugin.getAuthenticationManager().getPassword(authId);
		if (password == null) {
		    password = new char[0];
			promptPassword(title, message);
		}
	}

	protected final void setCaching(boolean enabled) {
		if ((fileInfoCache != null) == enabled) {
			return;
		}
		if (enabled) {
			fileInfoCache = new ExpiringMap<IPath, ExtendedFileInfo>(CACHE_TTL);
			fileInfosCache = new ExpiringMap<IPath, ExtendedFileInfo[]>(CACHE_TTL);
		} else {
			fileInfoCache = null;
			fileInfosCache = null;
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.IConnectionFileManager#fetchInfo(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized IExtendedFileInfo fetchInfo(IPath path, int options, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(MessageFormat.format(Messages.BaseConnectionFileManager_gethering_details, path.toPortableString()), 2);
		try {
			ExtendedFileInfo fileInfo = getCachedFileInfo(path);
			if (fileInfo == null) {
				testOrConnect(monitor);
				try {
					fileInfo = fetchAndCacheFileInfo(path, options, monitor);
				} finally {
					setLastOperationTime();
				}
			}
			return (IExtendedFileInfo) fileInfo.clone();
		} finally {
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.IConnectionFileManager#childNames(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized String[] childNames(IPath path, int options, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(MessageFormat.format(Messages.BaseConnectionFileManager_listing_directory, path.toPortableString()), 2);
		try {
			ExtendedFileInfo[] fileInfos = getCachedFileInfos(path);
			if (fileInfos != null) {
				List<String> list = new ArrayList<String>();
				for (ExtendedFileInfo fileInfo : fileInfos) {
					list.add(fileInfo.getName());
				}
				return list.toArray(new String[list.size()]);
			}
			testOrConnect(monitor);
			try {
				return listDirectory(basePath.append(path), monitor);
			} finally {
				setLastOperationTime();
			}
		} catch (FileNotFoundException e) {
			return new String[0];
		} finally {
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.IConnectionFileManager#childInfos(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized IExtendedFileInfo[] childInfos(IPath path, int options, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(MessageFormat.format(Messages.BaseConnectionFileManager_gethering_details, path.toPortableString()), 2);
		options = (options & IExtendedFileStore.DETAILED);
		try {
			ExtendedFileInfo[] fileInfos = getCachedFileInfos(path);
			if (fileInfos == null) {
				testOrConnect(monitor);
				try {
					fileInfos = cache(path, fetchFiles(basePath.append(path), options, monitor));
					for (ExtendedFileInfo fileInfo : fileInfos) {
						postProcessFileInfo(fileInfo, basePath.append(path), options, monitor);
						cache(path.append(fileInfo.getName()), fileInfo);
					}
				} catch (FileNotFoundException e) {
					return new IExtendedFileInfo[0];
				} catch (PermissionDeniedException e) {
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							MessageFormat.format(Messages.BaseConnectionFileManager_PermissionDenied0, path.toPortableString()), e));
				} finally {
					setLastOperationTime();
				}
			}
			return fileInfos.clone();
		} finally {
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.IConnectionFileManager#openInputStream(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized InputStream openInputStream(IPath path, int options, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(MessageFormat.format(Messages.BaseConnectionFileManager_opening_file, path.toPortableString()), 3);
		testOrConnect(monitor);
		try {
			ExtendedFileInfo fileInfo = fetchAndCacheFileInfo(path, Policy.subMonitorFor(monitor, 1));
			if (!fileInfo.exists()) {
				throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
						Messages.BaseConnectionFileManager_no_such_file, new FileNotFoundException(path.toPortableString())));
			}
			if (fileInfo.isDirectory()) {
				throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
						Messages.BaseConnectionFileManager_file_is_directory, new FileNotFoundException(path.toPortableString())));				
			}
			if (fileInfo.getLength() == 0) {
				return new ByteArrayInputStream(new byte[0]);
			}
			return readFile(basePath.append(path), Policy.subMonitorFor(monitor, 1));			
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.BaseConnectionFileManager_no_such_file, new FileNotFoundException(path.toPortableString())));
		} finally {
			setLastOperationTime();
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.IConnectionFileManager#openOutputStream(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized OutputStream openOutputStream(IPath path, int options, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(MessageFormat.format(Messages.BaseConnectionFileManager_opening_file, path.toPortableString()), 3);
		testOrConnect(monitor);
		try {
			ExtendedFileInfo fileInfo = fetchAndCacheFileInfo(path, Policy.subMonitorFor(monitor, 1));
			if (fileInfo.exists() && fileInfo.isDirectory()) {
				throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
						Messages.BaseConnectionFileManager_file_is_directory, new FileNotFoundException(path.toPortableString())));				
			}
			long permissions = -1;
			boolean useTemporary = canUseTemporaryFile(path, fileInfo, Policy.subMonitorFor(monitor, 1));
			if (fileInfo.exists()) {
				if (useTemporary) {
					permissions = fileInfo.getPermissions();
				}
			} else {
			    // new file; uses the user-defined default permissions
			    permissions = PreferenceUtils.getFilePermissions();
			}
			clearCache(path);
			return writeFile(basePath.append(path), useTemporary, permissions, Policy.subMonitorFor(monitor, 1));
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.BaseConnectionFileManager_parent_doesnt_exist, new FileNotFoundException(path.toPortableString())));
		} finally {
			setLastOperationTime();
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.IConnectionFileManager#delete(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized void delete(IPath path, int options, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor = new InfiniteProgressMonitor(monitor);
		monitor.beginTask(Messages.BaseConnectionFileManager_deleting, 20);
		testOrConnect(monitor);
		try {
			ExtendedFileInfo fileInfo = getCachedFileInfo(path);
			if (fileInfo == null) {
				fileInfo = fetchAndCacheFileInfo(path, Policy.subMonitorFor(monitor, 1));
			}
			if (!fileInfo.exists()) {
				return;
			}
			Policy.checkCanceled(monitor);
			try {
				if (fileInfo.isDirectory()) {
					deleteDirectory(basePath.append(path), monitor);
				} else {
					deleteFile(basePath.append(path), monitor);				
				}
			} catch (FileNotFoundException ignore) {
			} finally {
				clearCache(path);
			}
		} finally {
			setLastOperationTime();
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.IConnectionFileManager#mkdir(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized void mkdir(IPath path, int options, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(MessageFormat.format(Messages.BaseConnectionFileManager_creating_folder, path.toPortableString()), 3);
		testOrConnect(monitor);
		try {
			ExtendedFileInfo fileInfo = fetchAndCacheFileInfo(path, Policy.subMonitorFor(monitor, 1));
			if (fileInfo.exists()) {
				if (!fileInfo.isDirectory()) {
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_file_already_exists, new FileNotFoundException(path.toPortableString())));				
				}
				return;
			}
			if ((options & EFS.SHALLOW) != 0 && path.segmentCount() > 1) {
				fileInfo = fetchAndCacheFileInfo(path.removeLastSegments(1), Policy.subMonitorFor(monitor, 1));
				if (!fileInfo.exists()) {
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_parent_doesnt_exist, new FileNotFoundException(path.toPortableString())));					
				}
				if (!fileInfo.isDirectory()) {
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_parent_is_not_directory, new FileNotFoundException(path.toPortableString())));				
				}
				createDirectory(basePath.append(path), Policy.subMonitorFor(monitor, 1));
			} else if (path.segmentCount() == 1) {
				createDirectory(basePath.append(path), Policy.subMonitorFor(monitor, 1));
			} else {
				IProgressMonitor subMonitor = Policy.subMonitorFor(monitor, 1);
				subMonitor.beginTask(Messages.BaseConnectionFileManager_creating_folders, path.segmentCount());
				for (int i = path.segmentCount() - 1; i >= 0; --i) {
					createDirectory(basePath.append(path).removeLastSegments(i), subMonitor);
					subMonitor.worked(1);
				}
				subMonitor.done();
			}
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.BaseConnectionFileManager_parent_doesnt_exist, e));
		} finally {
			setLastOperationTime();
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.IConnectionFileManager#putInfo(org.eclipse.core.runtime.IPath, org.eclipse.core.filesystem.IFileInfo, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized void putInfo(IPath path, IFileInfo info, int options, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(MessageFormat.format(Messages.BaseConnectionFileManager_putting_changes, path.toPortableString()), 5);
		testOrConnect(monitor);
		try {
			if ((options & EFS.SET_LAST_MODIFIED) != 0) {
				setModificationTime(basePath.append(path), info.getLastModified(), Policy.subMonitorFor(monitor, 1));
			}
			if ((options & EFS.SET_ATTRIBUTES) != 0 && (options & IExtendedFileInfo.SET_PERMISSIONS) == 0) {
				ExtendedFileInfo fileInfo = fetchAndCacheFileInfo(path, Policy.subMonitorFor(monitor, 1));
				if (fileInfo.exists()) {
					long permissions = fileInfo.getPermissions();
					if (!info.getAttribute(EFS.ATTRIBUTE_READ_ONLY)) {
						permissions |= IExtendedFileInfo.PERMISSION_OWNER_WRITE;
					} else {
						permissions &= ~IExtendedFileInfo.PERMISSION_OWNER_WRITE;
					}
					if (info.getAttribute(EFS.ATTRIBUTE_EXECUTABLE)) {
						permissions |= IExtendedFileInfo.PERMISSION_OWNER_EXECUTE;
					} else {
						permissions &= ~IExtendedFileInfo.PERMISSION_OWNER_EXECUTE;
					}
					changeFilePermissions(basePath.append(path), permissions, Policy.subMonitorFor(monitor, 1));
				}
			}
			if (info instanceof IExtendedFileInfo) {
				IExtendedFileInfo extInfo = (IExtendedFileInfo) info;
				if ((options & IExtendedFileInfo.SET_PERMISSIONS) != 0) {
					changeFilePermissions(basePath.append(path), extInfo.getPermissions(), Policy.subMonitorFor(monitor, 1));
				}
				if ((options & IExtendedFileInfo.SET_GROUP) != 0) {
					changeFileGroup(basePath.append(path), extInfo.getGroup(), Policy.subMonitorFor(monitor, 1));
				}
			}
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.BaseConnectionFileManager_no_such_file, new FileNotFoundException(path.toPortableString())));
		} finally {
			clearCache(path);
			setLastOperationTime();
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.vfs.IConnectionFileManager#move(org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final synchronized void move(IPath sourcePath, IPath destinationPath, int options, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(MessageFormat.format(Messages.BaseConnectionFileManager_moving, sourcePath.toPortableString()), 5);
		testOrConnect(monitor);
		try {
			ExtendedFileInfo fileInfo = fetchAndCacheFileInfo(sourcePath, Policy.subMonitorFor(monitor, 1));
			if (!fileInfo.exists()) {
				throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
						Messages.BaseConnectionFileManager_no_such_file, new FileNotFoundException(sourcePath.toPortableString())));
			}
			boolean isDirectory = fileInfo.isDirectory();
			fileInfo = fetchAndCacheFileInfo(destinationPath, Policy.subMonitorFor(monitor, 1));
			if (fileInfo.exists()) {
				if ((options & EFS.OVERWRITE) == 0) {
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_file_already_exists, new FileNotFoundException(destinationPath.toPortableString())));
				}
				if (fileInfo.isDirectory() != isDirectory) {
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_cant_move));				
				}
			} else {
				fileInfo = fetchAndCacheFileInfo(destinationPath.removeLastSegments(1), Policy.subMonitorFor(monitor, 1));
				if (!fileInfo.exists()) {
					throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
							Messages.BaseConnectionFileManager_parent_doesnt_exist, new FileNotFoundException(destinationPath.toPortableString())));					
				}
			}
			clearCache(sourcePath);
			clearCache(destinationPath);
			renameFile(basePath.append(sourcePath), basePath.append(destinationPath), Policy.subMonitorFor(monitor, 2));
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					Messages.BaseConnectionFileManager_no_such_file, new FileNotFoundException(sourcePath.toPortableString())));
		} finally {
			setLastOperationTime();
			monitor.done();
		}
	}

	
	protected abstract void testConnection();
	protected abstract boolean canUseTemporaryFile(IPath path, ExtendedFileInfo fileInfo, IProgressMonitor monitor);
	
	// all methods here accept absolute path
	protected abstract void changeCurrentDir(IPath path) throws Exception, IOException, CoreException;
	protected abstract ExtendedFileInfo fetchFile(IPath path, int options, IProgressMonitor monitor) throws CoreException, FileNotFoundException, PermissionDeniedException;
	protected abstract ExtendedFileInfo[] fetchFiles(IPath path, int options, IProgressMonitor monitor) throws CoreException, FileNotFoundException, PermissionDeniedException;
	protected abstract String[] listDirectory(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException;
	protected abstract InputStream readFile(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException;
	protected abstract OutputStream writeFile(IPath path, boolean useTemporary, long permissions, IProgressMonitor monitor) throws CoreException, FileNotFoundException;
	protected abstract void createFile(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException, PermissionDeniedException;
	protected abstract void createDirectory(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException;
	protected abstract void renameFile(IPath sourcePath, IPath destinationPath, IProgressMonitor monitor) throws CoreException, FileNotFoundException;
	protected abstract void deleteFile(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException;
	protected abstract void deleteDirectory(IPath path, IProgressMonitor monitor) throws CoreException, FileNotFoundException;
	protected abstract void setModificationTime(IPath path, long modificationTime, IProgressMonitor monitor) throws CoreException, FileNotFoundException;
	protected abstract void changeFilePermissions(IPath path, long permissions, IProgressMonitor monitor) throws CoreException, FileNotFoundException;
	protected abstract void changeFileGroup(IPath path, String group, IProgressMonitor monitor) throws CoreException, FileNotFoundException;


	private ExtendedFileInfo fetchAndCacheFileInfo(IPath path, IProgressMonitor monitor) throws CoreException {
		return fetchAndCacheFileInfo(path, EFS.NONE, monitor);
	}

	private ExtendedFileInfo fetchAndCacheFileInfo(IPath path, int options, IProgressMonitor monitor) throws CoreException {
		ExtendedFileInfo fileInfo;
		try {
			fileInfo = fetchFile(basePath.append(path), options, monitor);
		} catch (FileNotFoundException e) {
			fileInfo = new ExtendedFileInfo(path.segmentCount() > 0 ? path.lastSegment() : Path.ROOT.toPortableString());
			fileInfo.setExists(false);
			return fileInfo;
		} catch (PermissionDeniedException e) {
			throw new CoreException(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID,
					MessageFormat.format(Messages.BaseConnectionFileManager_PermissionDenied0, basePath.append(path).toPortableString()), e));
		}
		if (path.segmentCount() == 0) {
			fileInfo.setName(Path.ROOT.toPortableString());
		}
		postProcessFileInfo(fileInfo, path, options, monitor);
		return cache(path, fileInfo);
	}

	private void postProcessFileInfo(ExtendedFileInfo fileInfo, IPath dirPath, int options, IProgressMonitor monitor) throws CoreException {
		if (fileInfo.getAttribute(EFS.ATTRIBUTE_SYMLINK)) {
			try {
				ExtendedFileInfo targetFileInfo = resolveSymlink(dirPath, fileInfo.getStringAttribute(EFS.ATTRIBUTE_LINK_TARGET), options, monitor);
				fileInfo.setExists(targetFileInfo.exists());
				if (targetFileInfo.exists()) {
					fileInfo.setDirectory(targetFileInfo.isDirectory());
					fileInfo.setLength(targetFileInfo.getLength());
					fileInfo.setLastModified(targetFileInfo.getLastModified());
					fileInfo.setOwner(targetFileInfo.getOwner());
					fileInfo.setGroup(targetFileInfo.getGroup());
					fileInfo.setPermissions(targetFileInfo.getPermissions());
				} else {
					throw new FileNotFoundException();
				}
			} catch (FileNotFoundException e) {
				try {
					changeCurrentDir(dirPath.append(fileInfo.getName()));
					fileInfo.setExists(true);
					fileInfo.setDirectory(true);
				} catch (FileNotFoundException fnfe) {
					fileInfo.setExists(false);
				} catch (Exception ignore) {
					CoreIOPlugin.log(new Status(IStatus.WARNING, CoreIOPlugin.PLUGIN_ID, Messages.BaseConnectionFileManager_symlink_resolve_failed, e));
				}
			}
		}
		long permissions = fileInfo.getPermissions();
		fileInfo.setAttribute(EFS.ATTRIBUTE_READ_ONLY, (permissions & IExtendedFileInfo.PERMISSION_OWNER_WRITE) == 0);
		fileInfo.setAttribute(EFS.ATTRIBUTE_EXECUTABLE, (permissions & IExtendedFileInfo.PERMISSION_OWNER_EXECUTE) != 0);
	}
	
	private ExtendedFileInfo resolveSymlink(IPath dirPath, String linkTarget, int options, IProgressMonitor monitor) throws CoreException, FileNotFoundException {
		Set<IPath> visited = new HashSet<IPath>();
		visited.add(dirPath);
		while (linkTarget != null && linkTarget.length() > 0) {
			IPath targetPath = Path.fromPortableString(linkTarget);
			if (!targetPath.isAbsolute()) {
				targetPath = dirPath.append(targetPath);
			}
			if (visited.contains(targetPath)) {
				break;
			}
			visited.add(targetPath);
			ExtendedFileInfo targetFileInfo = getCachedFileInfo(targetPath);
			if (targetFileInfo == null) {
				Policy.checkCanceled(monitor);
				try {
					targetFileInfo = cache(targetPath, fetchFile(targetPath, options, Policy.subMonitorFor(monitor, 1)));
				} catch (PermissionDeniedException e) {
					// permission denied is like file not found for the case of symlink resolving
					throw new FileNotFoundException(targetPath.toPortableString());
				}
			}
			cache(targetPath, targetFileInfo);
			if (targetFileInfo.getAttribute(EFS.ATTRIBUTE_SYMLINK)) {
				linkTarget = targetFileInfo.getStringAttribute(EFS.ATTRIBUTE_LINK_TARGET);
				dirPath = targetPath.removeLastSegments(1);
				continue;
			}
			return targetFileInfo;
		}
		return new ExtendedFileInfo();
	}

	private final ExtendedFileInfo getCachedFileInfo(IPath path) {
		return fileInfoCache != null ? fileInfoCache.get(path) : null;
	}

	private final ExtendedFileInfo[] getCachedFileInfos(IPath path) {
		return fileInfosCache !=  null ? fileInfosCache.get(path) : null;
	}

	private final ExtendedFileInfo cache(IPath path, ExtendedFileInfo fileInfo) {
		if (fileInfoCache != null && fileInfo.exists()) {
			fileInfoCache.put(path, fileInfo);
		}
		return fileInfo;
	}

	private final ExtendedFileInfo[] cache(IPath path, ExtendedFileInfo[] fileInfos) {
		if (fileInfosCache != null) {
			fileInfosCache.put(path, fileInfos);
		}
		return fileInfos;
	}

	protected void clearCache(IPath path) {
		int segments = path.segmentCount();
		if (fileInfoCache !=  null) {
			for (IPath p : new ArrayList<IPath>(fileInfoCache.keySet())) {
				if (p.segmentCount() >= segments && path.matchingFirstSegments(p) == segments) {
					fileInfoCache.remove(p);
				}
			}
		}
		if (fileInfosCache != null) {
			for (IPath p : new ArrayList<IPath>(fileInfosCache.keySet())) {
				if (p.segmentCount() >= segments && path.matchingFirstSegments(p) == segments) {
					fileInfosCache.remove(p);
				}
			}
		}
	}

	protected final void cleanup() {
		if (fileInfoCache != null) {
			fileInfoCache.clear();
		}
		if (fileInfosCache != null) {
			fileInfosCache.clear();
		}
	}
	
	protected void setLastOperationTime() {
	}
	
	protected final void testOrConnect(IProgressMonitor monitor) throws CoreException {
		Policy.checkCanceled(monitor);
		testConnection();
		if (!isConnected()) {
			connect(Policy.subMonitorFor(monitor, 1));
			Policy.checkCanceled(monitor);
		}
	}

}
