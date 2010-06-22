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

package com.aptana.ide.core.io.efs;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.internal.filesystem.Policy;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.preferences.CloakingUtils;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public final class EFSUtils
{

	/**
	 * 
	 */
	private EFSUtils()
	{
	}

	public static IFileStore getFileStore(IResource resource)
	{
		return WorkspaceFileSystem.getInstance().getStore(resource.getFullPath());
	}

	public static IFileStore getLocalFileStore(File file)
	{
		return EFS.getLocalFileSystem().fromLocalFile(file);
	}

	/**
	 * Sets the modification time of the client file
	 * 
	 * @param serverFile
	 * @param clientFile
	 * @throws CoreException
	 */
	public static void setModificationTime(long modifiedTime, IFileStore destFile) throws CoreException
	{
		IFileInfo fi = new FileInfo();
		fi.setLastModified(modifiedTime);
		destFile.putInfo(fi, EFS.SET_LAST_MODIFIED, null);
	}

	/**
	 * Returns the child files of the filestore
	 * 
	 * @param file
	 * @return
	 * @throws CoreException
	 */
	public static IFileStore[] getFiles(IFileStore file) throws CoreException
	{
		return getFiles(file, false, true);
	}

	/**
	 * Returns the child files of the filestore
	 * 
	 * @param file
	 * @return
	 * @throws CoreException
	 */
	public static IFileStore[] getFiles(IFileStore file, IProgressMonitor monitor) throws CoreException
	{
		return getFiles(file, false, true, monitor);
	}

	/**
	 * Returns the child files of the filestore
	 * 
	 * @param file
	 * @param recurse
	 *            Do we recurse through sub-directories?
	 * @param includeCloakedFiles
	 *            Do we include cloaked files in the list?
	 * @return
	 * @throws CoreException
	 */
	public static IFileStore[] getFiles(IFileStore file, boolean recurse, boolean includeCloakedFiles)
			throws CoreException
	{
		return getFiles(file, recurse, includeCloakedFiles, null);
	}

	/**
	 * Returns the absolute path of this file, from the root of the filestore.
	 * 
	 * @param file
	 * @return
	 */
	public static String getAbsolutePath(IFileStore file)
	{
		return file.toURI().getPath();
	}

	/**
	 * Returns the path of this file relative to the parent
	 * 
	 * @param file
	 * @return
	 * @throws CoreException
	 */
	public static String getRelativePath(IFileStore parent, IFileStore file)
	{
		if (parent.equals(file) || parent.isParentOf(file))
		{
			String rootFile = getAbsolutePath(parent);
			String childFile = getAbsolutePath(file);
			return childFile.substring(rootFile.length());
		}
		return null;
	}

	/**
	 * Creates the file on the destination store using a relative path
	 * 
	 * @param sourceRoot
	 * @param sourceStore
	 * @param destinationRoot
	 * @return
	 */
	public static IFileStore createFile(IFileStore sourceRoot, IFileStore sourceStore, IFileStore destinationRoot)
	{
		String relativePath = getRelativePath(sourceRoot, sourceStore);
		if (relativePath != null)
		{
			return destinationRoot.getFileStore(new Path(relativePath));
		}
		return null;
	}

	/**
	 * Returns the parent file of this file
	 * 
	 * @param file
	 * @return
	 * @throws CoreException
	 */
	public static String getRelativePath(IConnectionPoint point, IFileStore file)
	{
		try
		{
			return getRelativePath(point.getRoot(), file);
		}
		catch (CoreException e)
		{
			return null;
		}
	}

	/**
	 * @param sourceStore
	 *            the file to be copied
	 * @param destinationStore
	 *            the destination location
	 * @param monitor
	 *            the progress monitor
	 * @return true if the file is successfully copied, false if the operation did not go through for any reason
	 * @throws CoreException
	 */
	public static boolean copyFile(IFileStore sourceStore, IFileStore destinationStore, IProgressMonitor monitor)
			throws CoreException
	{
		if (sourceStore == null || CloakingUtils.isFileCloaked(sourceStore))
		{
			return false;
		}

		monitor = Policy.monitorFor(monitor);

		boolean success = true;
		monitor.subTask(MessageFormat.format("Copying {0} to {1}", sourceStore.getName(), destinationStore.getName()));

		sourceStore.copy(destinationStore, EFS.OVERWRITE, monitor);
		return success;
	}

	/**
	 * @param sourceStore
	 *            the file to be copied
	 * @param destinationStore
	 *            the destination location
	 * @param monitor
	 *            the progress monitor
	 * @param info
	 *            info to transfer
	 * @return true if the file is successfully copied, false if the operation did not go through for any reason
	 * @throws CoreException
	 */
	public static boolean copyFileWithAttributes(IFileStore sourceStore, IFileStore destinationStore,
			IProgressMonitor monitor, IFileInfo info) throws CoreException
	{
		boolean success = copyFile(sourceStore, destinationStore, monitor);
		if (success)
		{
			EFSUtils.setModificationTime(info.getLastModified(), destinationStore);
		}
		return success;
	}

	/**
	 * @throws CoreException
	 * @see {@link IConnectionPoint}#getFiles(IFileStore, boolean, boolean)
	 */
	public static IFileStore[] getFiles(IFileStore file, boolean recurse, boolean includeCloakedFiles,
			IProgressMonitor monitor) throws CoreException
	{
		IFileStore[] result = null;
		ArrayList<IFileStore> list = new ArrayList<IFileStore>();
		getFiles(file, recurse, list, includeCloakedFiles, monitor);
		result = list.toArray(new IFileStore[0]);
		return result;
	}

	/**
	 * Returns the child files of the filestore array
	 * 
	 * @param files
	 * @return
	 * @throws CoreException
	 */
	public static IFileStore[] getFiles(IFileStore[] files, boolean recurse, boolean includeCloakedFiles,
			IProgressMonitor monitor) throws CoreException
	{
		ArrayList<IFileStore> fileList = new ArrayList<IFileStore>();
		for (int i = 0; i < files.length; i++)
		{
			fileList.addAll(Arrays.asList(getFiles(files[i], recurse, includeCloakedFiles, monitor)));
		}
		return fileList.toArray(new IFileStore[0]);
	}
	
	/**
	 * Returns the files of the filestore array plus all of their children
	 * 
	 * @param files
	 * @return
	 * @throws CoreException
	 */
	public static IFileStore[] getAllFiles(IFileStore[] files, boolean recurse, boolean includeCloakedFiles,
			IProgressMonitor monitor) throws CoreException
	{
		ArrayList<IFileStore> fileList = new ArrayList<IFileStore>();
		fileList.addAll(Arrays.asList(files));
		IFileStore[] childFiles = getFiles(files, true, false, monitor);
		fileList.addAll(Arrays.asList(childFiles));
		return fileList.toArray(new IFileStore[0]);
	}	
	
	/**
	 * getFiles
	 * 
	 * @param file
	 * @param recurse
	 * @param list
	 * @throws CoreException
	 */
	private static void getFiles(IFileStore file, boolean recurse, List<IFileStore> list, boolean includeCloakedFiles,
			IProgressMonitor monitor) throws CoreException
	{
		if (file == null)
		{
			return;
		}

		monitor = Policy.monitorFor(monitor);
		Policy.checkCanceled(monitor);

		IFileStore[] children = file.childStores(EFS.NONE, monitor);

		if (children != null)
		{
			boolean addingFile;
			for (int i = 0; i < children.length; i++)
			{
				Policy.checkCanceled(monitor);
				IFileStore child = children[i];
				addingFile = false;
				if (includeCloakedFiles || !CloakingUtils.isFileCloaked(child))
				{
					list.add(child);
					addingFile = true;
					monitor.worked(1);
				}

				if (recurse && child.fetchInfo(EFS.NONE, monitor).isDirectory() && addingFile)
				{
					monitor.subTask(MessageFormat.format("Fetching children of {0}", child.getName()));
					getFiles(child, recurse, list, includeCloakedFiles, monitor);
				}
			}
		}
	}
}
