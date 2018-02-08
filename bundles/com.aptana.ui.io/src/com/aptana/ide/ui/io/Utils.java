/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.io.vfs.IExtendedFileStore;

/**
 * @author Michael Xia (mxia@aptana.com)
 * @author Max Stepanov
 */
public class Utils
{

	public static IFileStore getFileStore(Object adaptable)
	{
		if (adaptable instanceof IAdaptable)
		{
			IResource resource = (IResource) ((IAdaptable) adaptable).getAdapter(IResource.class);
			if (resource != null)
			{
				return EFSUtils.getFileStore(resource);
			}
		}
		return FileSystemUtils.getFileStore(adaptable);
	}

	public static IFileInfo getDetailedFileInfo(IAdaptable adaptable)
	{
		return getFileInfo(adaptable, IExtendedFileStore.DETAILED);
	}

	public static boolean exists(IAdaptable adaptable)
	{
		return getFileInfo(adaptable, IExtendedFileStore.EXISTENCE).exists();
	}

	public static boolean isDirectory(IAdaptable adaptable)
	{
		return getFileInfo(adaptable, IExtendedFileStore.EXISTENCE).isDirectory();
	}

	public static IFileInfo getFileInfo(IAdaptable adaptable, int options)
	{
		IFileInfo fileInfo = (IFileInfo) adaptable.getAdapter(IFileInfo.class);
		if (fileInfo == null)
		{
			IFileStore fileStore = getFileStore(adaptable);
			if (fileStore != null)
			{
				fileInfo = FileSystemUtils.fetchFileInfo(fileStore, options);
			}
		}
		return fileInfo;
	}
}
