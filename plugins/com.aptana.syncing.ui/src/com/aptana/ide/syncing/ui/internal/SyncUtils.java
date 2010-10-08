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
package com.aptana.ide.syncing.ui.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.ui.io.FileSystemUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class SyncUtils
{

	/**
	 * Computes the intersection of an array of sets.
	 * 
	 * @param sets
	 *            the array of sets
	 * @return a result set that contains the intersection
	 */
	public static Set<ISiteConnection> getIntersection(Set<ISiteConnection>[] sets)
	{
		Set<ISiteConnection> intersectionSet = new HashSet<ISiteConnection>();

		for (Set<ISiteConnection> set : sets)
		{
			intersectionSet.addAll(set);
		}
		for (Set<ISiteConnection> set : sets)
		{
			intersectionSet.retainAll(set);
		}

		return intersectionSet;
	}

	/**
	 * @param adaptable
	 *            the IAdaptable object
	 * @return the file store corresponding to the object
	 */
	public static IFileStore getFileStore(IAdaptable adaptable)
	{
		if (adaptable instanceof IResource)
		{
			IResource resource = (IResource) adaptable;
			return EFSUtils.getFileStore(resource);
		}
		return (IFileStore) adaptable.getAdapter(IFileStore.class);
	}

	/**
	 * @param adaptable
	 *            the IAdaptable object
	 * @return the array of file stores corresponding to the object
	 */
	public static IFileStore[] getFileStores(IAdaptable[] adaptable)
	{
		IFileStore[] fileStores = new IFileStore[adaptable.length];
		for (int i = 0; i < fileStores.length; ++i)
		{
			fileStores[i] = SyncUtils.getFileStore(adaptable[i]);
		}
		return fileStores;
	}

	/**
	 * @param adaptable
	 *            the IAdaptable object
	 * @return the file info corresponding to the object
	 */
	public static IFileInfo getFileInfo(IAdaptable adaptable)
	{
		IFileInfo fileInfo = (IFileInfo) adaptable.getAdapter(IFileInfo.class);
		if (fileInfo == null)
		{
			IFileStore fileStore = getFileStore(adaptable);
			if (fileStore != null)
			{
				fileInfo = FileSystemUtils.fetchFileInfo(fileStore);
			}
		}
		return fileInfo;
	}

	public static IConnectionPoint findOrCreateConnectionPointFor(IAdaptable adaptable)
	{
		if (adaptable == null)
		{
			return null;
		}
		IConnectionPoint connectionPoint = (IConnectionPoint) adaptable.getAdapter(IConnectionPoint.class);
		if (connectionPoint != null)
		{
			return connectionPoint;
		}
		IResource resource = (IResource) adaptable.getAdapter(IResource.class);
		if (resource == null)
		{
			resource = (IResource) adaptable.getAdapter(IContainer.class);
		}
		if (resource instanceof IContainer)
		{
			return ConnectionPointUtils.findOrCreateWorkspaceConnectionPoint((IContainer) resource);
		}
		else if (resource != null)
		{
			return ConnectionPointUtils.findOrCreateWorkspaceConnectionPoint(resource.getParent());
		}
		else
		{
			File file = (File) adaptable.getAdapter(File.class);
			if (file != null)
			{
				return ConnectionPointUtils.findOrCreateLocalConnectionPoint(Path.fromOSString(file.getAbsolutePath()));
			}
		}
		return null;
	}

	public static IFileStore[] getUploadFiles(IConnectionPoint sourceManager, IConnectionPoint destManager,
			IFileStore[] files, IProgressMonitor monitor) throws IOException, CoreException
	{
		Set<IFileStore> newFiles = new HashSet<IFileStore>();

		// show be done via some sort of "import"
		IFileStore file;
		IFileStore[] parents;
		IFileStore file2;
		for (int i = 0; i < files.length; i++)
		{
			file = files[i];
			parents = getParentDirectories(file, sourceManager);

			for (int j = 0; j < parents.length; j++)
			{
				file2 = parents[j];
				IFileStore newFile = EFSUtils.createFile(sourceManager.getRoot(), file2, destManager.getRoot());

				if (!newFiles.contains(newFile))
				{
					newFiles.add(newFile);
				}
			}

			if (file.fetchInfo().isDirectory())
			{
				IFileStore newFile = EFSUtils.createFile(sourceManager.getRoot(), file, destManager.getRoot());
				if (!newFiles.contains(newFile))
				{
					newFiles.add(newFile);
				}
				if (newFile.fetchInfo().exists())
				{
					newFiles.addAll(Arrays.asList(EFSUtils.getFiles(newFile, true, false, null)));
				}
			}
			else
			{
				IFileStore newFile = EFSUtils.createFile(sourceManager.getRoot(), file, destManager.getRoot());
				if (!newFiles.contains(newFile))
				{
					newFiles.add(newFile);
				}
			}
		}

		return newFiles.toArray(new IFileStore[newFiles.size()]);
	}

	public static IFileStore[] getDownloadFiles(IConnectionPoint sourceManager, IConnectionPoint destManager,
			IFileStore[] files, boolean ignoreError, IProgressMonitor monitor)
	{
		return getDownloadFiles(sourceManager, destManager, files, true, ignoreError, monitor);
	}

	public static IFileStore[] getDownloadFiles(IConnectionPoint sourceManager, IConnectionPoint destManager,
			IFileStore[] files, boolean fromSource, boolean ignoreError, IProgressMonitor monitor)
	{
		Set<IFileStore> newFiles = new HashSet<IFileStore>();
		IFileStore newFile;
		for (IFileStore file : files)
		{
			newFile = null;
			try
			{
				if (file.fetchInfo().isDirectory())
				{
					if (fromSource)
					{
						newFile = EFSUtils.createFile(sourceManager.getRoot(), file, destManager.getRoot());
					}
					else
					{
						newFile = file;
					}
					if (newFile.fetchInfo().exists())
					{
						IFileStore[] f = EFSUtils.getFiles(newFile, true, false, null);
						if (!newFiles.contains(newFile))
						{
							newFiles.add(newFile);
						}
						newFiles.addAll(Arrays.asList(f));
					}
				}
				else
				{
					if (fromSource)
					{
						newFile = EFSUtils.createFile(sourceManager.getRoot(), file, destManager.getRoot());
					}
					else
					{
						newFile = file;
					}
					if (newFile.fetchInfo().exists())
					{
						if (!newFiles.contains(newFile))
						{
							newFiles.add(newFile);
						}
					}
				}
			}
			catch (CoreException e)
			{
				if (newFile != null && !ignoreError)
				{
					// SyncingConsole.println(StringUtils.format(Messages.FileDownloadAction_FileDoesNotExistAtRemoteSite,
					// newFile.getAbsolutePath())); // we ignore files that don't exist on the remote server
				}
			}
		}

		return newFiles.toArray(new IFileStore[newFiles.size()]);
	}

	/**
	 * Creates a list of all parent directories of the current file (or directory)
	 * 
	 * @param file
	 * @param sourceManager
	 * @return IVirtualFile[]
	 * @throws CoreException
	 */
	public static IFileStore[] getParentDirectories(IFileStore file, IConnectionPoint sourceManager)
			throws CoreException
	{
		List<IFileStore> parentDirs = new ArrayList<IFileStore>();

		if (sourceManager.getRoot().isParentOf(file))
		{
			IFileStore currentFile = file;

			while (currentFile != null)
			{
				if (currentFile.equals(sourceManager.getRoot()))
				{
					break;
				}

				parentDirs.add(0, currentFile); // add at beginning of list, as we want most "distant" folder first
				currentFile = currentFile.getParent();
			}
		}

		return parentDirs.toArray(new IFileStore[parentDirs.size()]);
	}
}
