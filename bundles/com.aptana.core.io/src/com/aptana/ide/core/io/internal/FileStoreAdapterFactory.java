/**
 * Aptana Studio
 * Copyright (c) 2020 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io.internal;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterFactory;

import com.aptana.core.util.ArrayUtil;

@SuppressWarnings({ "rawtypes" })
public class FileStoreAdapterFactory implements IAdapterFactory
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adaptableObject instanceof IFileStore)
		{
			IFileStore fileStore = ((IFileStore) adaptableObject);
			URI fileStoreURI = fileStore.toURI();
			if (IResource.class.equals(adapterType))
			{
				IFile file = toIFile(fileStoreURI);
				if (file != null)
				{
					return file;
				}
				return toIContainer(fileStoreURI);
			}

			if (IFile.class.equals(adapterType))
			{
				return toIFile(fileStoreURI);
			}

			if (IContainer.class.equals(adapterType))
			{
				return toIContainer(fileStoreURI);
			}
		}
		return null;
	}

	protected IContainer toIContainer(URI fileStoreURI)
	{
		IContainer[] containers = getWorkspaceRoot().findContainersForLocationURI(fileStoreURI);
		if (!ArrayUtil.isEmpty(containers))
		{
			return containers[0];
		}
		return null;
	}

	protected IFile toIFile(URI fileStoreURI)
	{
		IFile[] files = getWorkspaceRoot().findFilesForLocationURI(fileStoreURI);
		if (!ArrayUtil.isEmpty(files))
		{
			return files[0];
		}
		return null;
	}

	protected IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList()
	{
		return new Class[] { IResource.class, IFile.class, IContainer.class };
	}
}
