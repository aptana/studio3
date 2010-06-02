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

import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.IFileTree;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.vfs.IConnectionFileManager;
import com.aptana.ide.core.io.vfs.VirtualConnectionManager;

/**
 * @author Max Stepanov
 *
 */
public class VirtualFileSystem extends FileSystem {

	public static final String SCHEME_VIRTUAL = "aptanavfs"; //$NON-NLS-1$
	
	private static VirtualFileSystem instance;

	/**
	 * The attributes of this file system. The initial value of -1 is used
	 * to indicate that the attributes have not yet been computed.
	 */
	private int attributes = -1;

	/**
	 * 
	 */
	public VirtualFileSystem() {
		super();
		instance = this;
	}

	public static IFileSystem getInstance() {
		if (instance == null) {
			try {
				EFS.getFileSystem(SCHEME_VIRTUAL);
			} catch (CoreException e) {
				throw new Error(e);
			}
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#attributes()
	 */
	@Override
	public int attributes() {
		if (attributes != -1)
			return attributes;
		attributes = 0;
		attributes |= EFS.ATTRIBUTE_READ_ONLY;
		attributes |= EFS.ATTRIBUTE_EXECUTABLE;
		attributes |= EFS.ATTRIBUTE_SYMLINK | EFS.ATTRIBUTE_LINK_TARGET;
		return attributes;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#canDelete()
	 */
	@Override
	public boolean canDelete() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#canWrite()
	 */
	@Override
	public boolean canWrite() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#fetchFileTree(org.eclipse.core.filesystem.IFileStore, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileTree fetchFileTree(IFileStore root, IProgressMonitor monitor) {
		if (root instanceof VirtualFile) {
			try {
				return ((VirtualFile) root).fetchFileTree(null, monitor);
			} catch (CoreException e) {
				// TODO: this exception could be thrown after 3.6M1
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=280944
				CoreIOPlugin.log(new Status(IStatus.WARNING, CoreIOPlugin.PLUGIN_ID, Messages.VirtualFileSystem_ERR_FetchFileTree, e));
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#isCaseSensitive()
	 */
	@Override
	public boolean isCaseSensitive() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#getStore(org.eclipse.core.runtime.IPath)
	 */
	@Override
	public IFileStore getStore(IPath path) {
		return EFS.getNullFileSystem().getStore(path);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#getStore(java.net.URI)
	 */
	@Override
	public IFileStore getStore(URI uri) {
		if (SCHEME_VIRTUAL.equals(uri.getScheme())) {
			URI rootURI = VirtualConnectionManager.getVirtualRootURI(uri);
			IConnectionFileManager fileManager = VirtualConnectionManager.getInstance().getVirtualFileManager(rootURI);
			if (fileManager != null) {
				return new VirtualFile(fileManager, rootURI, new Path(uri.getPath()));
			}
		}
		return EFS.getNullFileSystem().getStore(uri);
	}
}
