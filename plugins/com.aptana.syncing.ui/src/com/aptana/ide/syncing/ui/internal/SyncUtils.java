/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.ui.io.Utils;

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
	 * @return the array of file stores corresponding to the object
	 */
	public static IFileStore[] getFileStores(IAdaptable[] adaptable)
	{
		IFileStore[] fileStores = new IFileStore[adaptable.length];
		for (int i = 0; i < fileStores.length; ++i)
		{
			fileStores[i] = Utils.getFileStore(adaptable[i]);
		}
		return fileStores;
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
