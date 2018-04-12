/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention
// $codepro.audit.disable thrownExceptions
// $codepro.audit.disable exceptionUsage.exceptionCreation

package com.aptana.core.io.efs;

import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.IFileTree;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.core.io.vfs.IConnectionFileManager;
import com.aptana.core.io.vfs.VirtualConnectionManager;
import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * @author Max Stepanov
 */
public class VirtualFileSystem extends FileSystem {

	public static final String SCHEME_VIRTUAL = "aptanavfs"; //$NON-NLS-1$

	private static VirtualFileSystem instance;

	/**
	 * The attributes of this file system. The initial value of -1 is used to
	 * indicate that the attributes have not yet been computed.
	 */
	private int attributes = -1;

	/**
	 * 
	 */
	public VirtualFileSystem() {
		super();
		setInstance(this);
	}

	private static void setInstance(VirtualFileSystem object) {
		instance = object;
	}

	public static IFileSystem getInstance() {
		if (instance == null) {
			try {
				EFS.getFileSystem(SCHEME_VIRTUAL);
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.filesystem.provider.FileSystem#canDelete()
	 */
	@Override
	public boolean canDelete() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.filesystem.provider.FileSystem#canWrite()
	 */
	@Override
	public boolean canWrite() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.filesystem.provider.FileSystem#fetchFileTree(org.eclipse
	 * .core.filesystem.IFileStore, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileTree fetchFileTree(IFileStore root, IProgressMonitor monitor) {
		if (root instanceof VirtualFile) {
			try {
				return ((VirtualFile) root).fetchFileTree(null, monitor);
			} catch (CoreException e) {
				// TODO: this exception could be thrown after 3.6M1
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=280944
				IdeLog.logWarning(CoreIOPlugin.getDefault(), Messages.VirtualFileSystem_ERR_FetchFileTree, e);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.filesystem.provider.FileSystem#isCaseSensitive()
	 */
	@Override
	public boolean isCaseSensitive() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.filesystem.provider.FileSystem#getStore(org.eclipse.
	 * core.runtime.IPath)
	 */
	@Override
	public IFileStore getStore(IPath path) {
		return EFS.getNullFileSystem().getStore(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.filesystem.provider.FileSystem#getStore(java.net.URI)
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
