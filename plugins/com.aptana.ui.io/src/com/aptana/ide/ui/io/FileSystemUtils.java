/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

package com.aptana.ide.ui.io;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreePath;

import com.aptana.ide.core.io.vfs.IExtendedFileInfo;
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
			fileStore = fileStore.getParent();
		}
		return new TreePath(list.toArray());
	}
	
	public static IFileInfo fetchFileInfo(IFileStore fileStore) throws OperationCanceledException {
		Job job = new FetchFileInfoJob(fileStore);
		job.setPriority(Job.SHORT);
		job.setSystem(true);
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
		if (result == Status.CANCEL_STATUS) {
			return null;
		}
		return null;
	}
}
