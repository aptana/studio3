/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.ui.internal;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;


/**
 * @author Max Stepanov
 */
public class LocalFileStorageEditorInput extends StorageEditorInput implements IPathEditorInput, ILocationProvider {

	/**
	 * Constructs an editor input for the given storage
	 * 
	 * @param storage
	 */
	public LocalFileStorageEditorInput(IStorage storage) {
		super(storage);
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		IStorage storage = getStorage();
		if (storage instanceof LocalFileStorage) {
			return ((LocalFileStorage) storage).getFile().exists();
		} else if (storage instanceof com.aptana.core.resources.LocalFileStorage) {
			return ((com.aptana.core.resources.LocalFileStorage) storage).getFile().exists();
		}
		return false;
	}

	/*
	 * @see org.eclipse.ui.IPathEditorInput#getPath()
	 */
	public IPath getPath() {
		IStorage storage = getStorage();
		if (storage instanceof LocalFileStorage) {
			return ((LocalFileStorage) getStorage()).getFullPath();
		} else if (storage instanceof com.aptana.core.resources.LocalFileStorage) {
			return ((com.aptana.core.resources.LocalFileStorage) storage).getFullPath();
		}
		return null;
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (ILocationProvider.class == adapter) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	/*
	 * @see org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
	 */
	public IPath getPath(Object element) {
		if (element instanceof LocalFileStorageEditorInput) {
			return ((LocalFileStorageEditorInput) element).getPath();
		}
		return null;
	}
}
