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
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.preferences.CloakingUtils;
import com.aptana.ide.core.io.vfs.IExtendedFileStore;

/**
 * @author Max Stepanov
 *
 */
public final class EFSUtils {

	/**
	 * 
	 */
	private EFSUtils() {
	}

	public static IFileStore getFileStore(IResource resource) {
		return WorkspaceFileSystem.getInstance().getStore(resource.getFullPath());
	}
	
	public static IFileStore getLocalFileStore(File file) {
		return EFS.getLocalFileSystem().fromLocalFile(file);
	}
	
	/**
	 * Sets the modification time of the client file
	 * @param serverFile
	 * @param clientFile
	 * @throws CoreException
	 */
	public static void setModificationTime(IFileStore sourceFile, IFileStore destFile) throws CoreException {
		IFileInfo fi = new FileInfo();
		fi.setLastModified(sourceFile.fetchInfo(IExtendedFileStore.DETAILED, null).getLastModified());
		destFile.putInfo(fi, EFS.SET_LAST_MODIFIED, null);
	}
	
	/**
	 * Returns the child files of the filestore
	 * @param file
	 * @return 
	 * @throws CoreException 
	 */
	public static IFileStore[] getFiles(IFileStore file) throws CoreException {
		return getFiles(file, false, true);
	}
	
	/**
	 * Returns the child files of the filestore
	 * @param file
	 * @return 
	 * @throws CoreException 
	 */
	public static IFileStore[] getFiles(IFileStore file, IProgressMonitor monitor) throws CoreException {
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
	public static IFileStore[] getFiles(IFileStore file, boolean recurse, boolean includeCloakedFiles) throws CoreException {
		return getFiles(file, recurse, includeCloakedFiles, null);
	}

	/**
	 * Returns the parent file of this file
	 * @param file
	 * @return
	 */
	public static String getAbsolutePath(IFileStore file) {
		// need to strip scheme (i.e. file:)
		String scheme = file.toURI().getScheme();
		return file.toURI().toString().substring(scheme.length() + 1);
	}

	/**
	 * Returns the parent file of this file
	 * 
	 * @param file
	 * @return
	 * @throws CoreException
	 */
	public static String getRelativePath(IFileStore parent, IFileStore file) {
		if (parent == file || parent.isParentOf(file)) {
			String rootFile = getAbsolutePath(parent);
			String childFile = getAbsolutePath(file);
			return childFile.substring(rootFile.length());
		}
		return null;
	}

	/**
	 * Creates the file on the destination store using a relative path
	 * @param sourceRoot
	 * @param sourceStore
	 * @param destinationRoot
	 * @return
	 */
	public static IFileStore createFile(IFileStore sourceRoot, IFileStore sourceStore, IFileStore destinationRoot) {
        String sourceRootPath = sourceRoot.toString();
        String sourcePath = sourceStore.toString();
        int index = sourcePath.indexOf(sourceRootPath);
        if (index > -1) {
            String relativePath = sourcePath.substring(index + sourceRootPath.length());
            return destinationRoot.getFileStore(new Path(relativePath));
        }
        return null;
	}

	/**
	 * Returns the parent file of this file
	 * @param file
	 * @return
	 * @throws CoreException 
	 */
	public static String getRelativePath(IConnectionPoint point, IFileStore file) {
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
     * @return true if the file is successfully copied, false if the operation
     *         did not go through for any reason
     * @throws CoreException 
     */
    public static boolean copyFile(IFileStore sourceStore, IFileStore destinationStore,
            IProgressMonitor monitor) throws CoreException {
        if (sourceStore == null || CloakingUtils.isFileCloaked(sourceStore)) {
            return false;
        }

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        boolean success = true;
        monitor.subTask(MessageFormat.format("Copying {0} to {1}", sourceStore
                .getName(), destinationStore.getName()));

        sourceStore.copy(destinationStore, EFS.OVERWRITE, monitor);
        return success;
    }

	/**
	 * @throws CoreException 
	 * @see {@link IConnectionPoint}#getFiles(IFileStore, boolean, boolean)
	 */
	public static IFileStore[] getFiles(IFileStore file, boolean recurse, boolean includeCloakedFiles, IProgressMonitor monitor) throws CoreException
	{
		IFileStore[] result = null;
		ArrayList<IFileStore> list = new ArrayList<IFileStore>();
		getFiles(file, recurse, list, includeCloakedFiles, monitor);
		result = list.toArray(new IFileStore[0]);
		return result;
	}

	/**
	 * getFiles
	 * 
	 * @param file
	 * @param recurse
	 * @param list
	 * @throws CoreException 
	 */
	private static void getFiles(IFileStore file, boolean recurse, List<IFileStore> list, boolean includeCloakedFiles, IProgressMonitor monitor) throws CoreException
	{
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

		if (file == null)
		{
			return;
		}

		IFileStore[] children = file.childStores(EFS.NONE, monitor);

		if (children != null)
		{
			boolean addingFile;
			for (int i = 0; i < children.length; i++)
			{
				IFileStore child = children[i];
				addingFile = false;
				if (includeCloakedFiles || !CloakingUtils.isFileCloaked(child))
				{
					list.add(child);
					addingFile = true;
				}

				if (recurse && child.fetchInfo(EFS.NONE, monitor).isDirectory() && addingFile)
				{
					getFiles(child, recurse, list, includeCloakedFiles, monitor);
				}
			}
		}
	}
	
	/**
	 * Returns the parent file of this file
	 * @param file
	 * @return
	 */
	public static IFileStore getParentFile(IFileStore file) {
		return file.getParent();
	}
	
	
}
