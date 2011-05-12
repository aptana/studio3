/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.internal;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.aptana.core.resources.IUniformResource;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("rawtypes")
public class FileStoreEditorInputAdapterFactory implements IAdapterFactory
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (IUniformResource.class == adapterType)
		{
			if (adaptableObject instanceof FileStoreEditorInput)
			{
				return new FileStoreEditorInputUniformResource((FileStoreEditorInput) adaptableObject);
			}
		}
		else if (IFile.class == adapterType)
		{
			if (adaptableObject instanceof FileStoreEditorInput)
			{
				URI uri = ((FileStoreEditorInput) adaptableObject).getURI();
				IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(uri);
				if (files.length == 1)
				{
					return files[0];
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList()
	{
		return new Class[] { IFile.class, IUniformResource.class };
	}

}
