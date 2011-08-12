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

import java.io.File;
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

import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * @author Max Stepanov
 */
public class WorkspaceFileSystem extends FileSystem {

	protected static final String SCHEME_WORKSPACE = "workspace"; //$NON-NLS-1$

	private static IFileSystem instance;

	/**
	 * 
	 */
	public WorkspaceFileSystem() {
		super();
		setInstance(this);
	}

	private static void setInstance(WorkspaceFileSystem object) {
		instance = object;
	}

	public static IFileSystem getInstance() {
		if (instance == null) {
			try {
				instance = EFS.getFileSystem(SCHEME_WORKSPACE);
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
	public int attributes() { // NO_UCD
		return EFS.getLocalFileSystem().attributes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.filesystem.provider.FileSystem#canDelete()
	 */
	@Override
	public boolean canDelete() { // NO_UCD
		return EFS.getLocalFileSystem().canDelete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.filesystem.provider.FileSystem#canWrite()
	 */
	@Override
	public boolean canWrite() { // NO_UCD
		return EFS.getLocalFileSystem().canWrite();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.filesystem.provider.FileSystem#fromLocalFile(java.io
	 * .File)
	 */
	@Override
	public IFileStore fromLocalFile(File file) {
		return WorkspaceFile.fromLocalFile(file);
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
		return new WorkspaceFile(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.filesystem.provider.FileSystem#getStore(java.net.URI)
	 */
	@Override
	public IFileStore getStore(URI uri) {
		return new WorkspaceFile(new Path(uri.getPath()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.filesystem.provider.FileSystem#fetchFileTree(org.eclipse
	 * .core.filesystem.IFileStore,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileTree fetchFileTree(IFileStore root, IProgressMonitor monitor) {
		if (root instanceof WorkspaceFile) {
			try {
				return ((WorkspaceFile) root).fetchFileTree(null, monitor);
			} catch (CoreException e) {
				// TODO: this exception could be thrown after 3.6M1
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=280944
				IdeLog.logWarning(CoreIOPlugin.getDefault(), Messages.WorkspaceFileSystem_FetchingTreeError, e);
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
		return EFS.getLocalFileSystem().isCaseSensitive();
	}
}
