/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.navigator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;

/**
 * @author Max Stepanov
 *
 */
public class FileSystemObject implements IAdaptable {

	private IFileStore fileStore;
	private IFileInfo fileInfo;
	
	/**
	 * 
	 */
	public FileSystemObject(IFileStore fileStore, IFileInfo fileInfo) {
		this.fileStore = fileStore;
		this.fileInfo = fileInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof FileSystemObject) {
			return fileStore.equals(((FileSystemObject) obj).fileStore);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return fileStore.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return fileStore.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (IFileStore.class.equals(adapter)) {
			return fileStore;
		} else if (IFileInfo.class.equals(adapter)) {
			return fileInfo;
		} else if (IDeferredWorkbenchAdapter.class.equals(adapter)
				|| IWorkbenchAdapter.class.equals(adapter)) {
			return FileSystemWorkbenchAdapter.getInstance();
		}
		return fileStore.getAdapter(adapter);
	}

	/**
	 * @return
	 * @see org.eclipse.core.filesystem.IFileInfo#getName()
	 */
	public String getName() {
		return fileStore.getName();
	}

	/**
	 * @return
	 * @see org.eclipse.core.filesystem.IFileInfo#isDirectory()
	 */
	public boolean isDirectory() {
		return fileInfo.isDirectory();
	}

	/**
	 * @return
	 */
	public boolean isSymlink() {
		return fileInfo.getAttribute(EFS.ATTRIBUTE_SYMLINK);
	}

	/**
	 * @return the store
	 */
	public IFileStore getFileStore() {
		return fileStore;
	}

	/**
	 * @return the fileInfo
	 */
	public IFileInfo getFileInfo() {
		return fileInfo;
	}

}
