/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.aptana.ide.ui.io.FileSystemUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemDotFilter extends ViewerFilter
{

	private static final String EXPRESSION = "."; //$NON-NLS-1$

	public FileSystemDotFilter()
	{
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (element instanceof IAdaptable)
		{
			IResource resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
			if (resource != null)
			{
				return !resource.getName().startsWith(EXPRESSION);
			}
		}
		IFileStore fileStore = FileSystemUtils.getFileStore(element);
		return fileStore == null || !fileStore.getName().startsWith(EXPRESSION);
	}
}
