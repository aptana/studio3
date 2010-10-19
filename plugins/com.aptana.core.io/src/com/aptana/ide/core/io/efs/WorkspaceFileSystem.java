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

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Max Stepanov
 *
 */
public class WorkspaceFileSystem extends FileSystem {

	protected static final String SCHEME_WORKSPACE = "workspace"; //$NON-NLS-1$

	private static WorkspaceFileSystem instance;
	
	/**
	 * 
	 */
	public WorkspaceFileSystem() {
		super();
		instance = this;
	}

	public static IFileSystem getInstance() {
		if (instance == null) {
			try {
				EFS.getFileSystem(SCHEME_WORKSPACE);
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
	public int attributes() { // NO_UCD
		return EFS.getLocalFileSystem().attributes();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#canDelete()
	 */
	@Override
	public boolean canDelete() { // NO_UCD
		return EFS.getLocalFileSystem().canDelete();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#canWrite()
	 */
	@Override
	public boolean canWrite() { // NO_UCD
		return EFS.getLocalFileSystem().canWrite();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#fromLocalFile(java.io.File)
	 */
	@Override
	public IFileStore fromLocalFile(File file) {
		return WorkspaceFile.fromLocalFile(file);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#getStore(org.eclipse.core.runtime.IPath)
	 */
	@Override
	public IFileStore getStore(IPath path) {
		return new WorkspaceFile(path);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#getStore(java.net.URI)
	 */
	@Override
	public IFileStore getStore(URI uri) {
		return new WorkspaceFile(new Path(uri.getPath()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#isCaseSensitive()
	 */
	@Override
	public boolean isCaseSensitive() {
		return EFS.getLocalFileSystem().isCaseSensitive();
	}
}
