/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreePath;

import com.aptana.core.io.vfs.IExtendedFileInfo;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ProgressMonitorInterrupter;
import com.aptana.ide.ui.io.internal.FetchFileInfoJob;
import com.aptana.ide.ui.io.internal.FetchFileInfoStatus;

/**
 * @author Max Stepanov
 *
 */
public final class FileSystemUtils {

	/**
	 * 
	 */
	private FileSystemUtils() {
	}

	public static IFileInfo getFileInfo(Object object) {
		if (object instanceof IFileInfo) {
			return (IFileInfo) object;
		} else if (object instanceof IAdaptable) {
			return (IFileInfo) ((IAdaptable) object).getAdapter(IFileInfo.class);
		}
		return null;
	}

	public static IFileStore getFileStore(Object object) {
		if (object instanceof IFileStore) {
			return (IFileStore) object;
		} else if (object instanceof IAdaptable) {
			return (IFileStore) ((IAdaptable) object).getAdapter(IFileStore.class);
		}
		return null;
	}

	public static boolean isDirectory(Object object) {
		IFileInfo fileInfo = getFileInfo(object);
		if (fileInfo != null) {
			return fileInfo.isDirectory();
		}
		return false;
	}

	public static boolean isSymlink(Object object) {
		IFileInfo fileInfo = getFileInfo(object);
		if (fileInfo != null) {
			return fileInfo.getAttribute(EFS.ATTRIBUTE_SYMLINK);
		}
		return false;
	}

	public static boolean isPrivate(Object object)
	{
		IFileInfo fileInfo = getFileInfo(object);
		if (fileInfo != null && fileInfo instanceof IExtendedFileInfo) {
			return ((IExtendedFileInfo)fileInfo).getPermissions() == 0;
		}
		return false;
	}

	public static URI getURI(Object object) {
		IFileStore fileStore = getFileStore(object);
		if (fileStore != null) {
			return fileStore.toURI();
		}
		return null;
	}
	
	public static TreePath createTreePath(IFileStore fileStore) {
		List<IFileStore> list = new ArrayList<IFileStore>();
		while (fileStore != null) {
			list.add(0, fileStore);
			fileStore = fileStore.getParent(); // $codepro.audit.disable questionableAssignment
		}
		return new TreePath(list.toArray());
	}
	
	public static IFileInfo fetchFileInfo(IFileStore fileStore, int options) throws OperationCanceledException {
		Job job = new FetchFileInfoJob(fileStore, options);
		job.setPriority(Job.SHORT);
		EclipseUtil.setSystemForJob(job);
		job.schedule();
		try {
			job.join();
		} catch (InterruptedException e) {
			return null;
		}
		IStatus result = job.getResult();
		if (result instanceof FetchFileInfoStatus) {
			return ((FetchFileInfoStatus) result).getFileInfo();
		}
		if (Status.CANCEL_STATUS.equals(result)) {
			return null;
		}
		return null;
	}

	public static IFileInfo[] childInfos(IFileStore fileStore, int options, IProgressMonitor monitor) throws CoreException {
		ProgressMonitorInterrupter interrupter = new ProgressMonitorInterrupter(monitor);
		try {
			return fileStore.childInfos(options, monitor);
		} finally {
			interrupter.dispose();
		}
	}
}
