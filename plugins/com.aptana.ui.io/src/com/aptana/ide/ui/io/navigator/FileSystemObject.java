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
		if (IFileStore.class == adapter) {
			return fileStore;
		} else if (IFileInfo.class == adapter) {
			return fileInfo;
		} else if (IDeferredWorkbenchAdapter.class == adapter
				|| IWorkbenchAdapter.class == adapter) {
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
