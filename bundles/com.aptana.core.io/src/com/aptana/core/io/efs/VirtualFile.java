/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable questionableAssignment
// $codepro.audit.disable questionableAssignment

package com.aptana.core.io.efs;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileTree;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.core.io.vfs.IConnectionFileManager;
import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.io.vfs.IFileTreeVisitor;
import com.aptana.core.io.vfs.Policy;
import com.aptana.core.util.URLEncoder;
import com.aptana.ide.core.io.InfiniteProgressMonitor;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class VirtualFile extends FileStore implements IExtendedFileStore {

	private IConnectionFileManager fileManager;
	private URI baseURI;
	private IPath path;
	
	/**
	 * 
	 */
	public VirtualFile(IConnectionFileManager fileManager, URI baseURI, IPath path) {
		this.fileManager = fileManager;
		this.baseURI = baseURI;
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#childNames(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public String[] childNames(int options, IProgressMonitor monitor) throws CoreException {
		return fileManager.childNames(path, options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#childInfos(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileInfo[] childInfos(int options, IProgressMonitor monitor) throws CoreException {
		return fileManager.childInfos(path, options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#childStores(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileStore[] childStores(int options, IProgressMonitor monitor) throws CoreException {
		return super.childStores(options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#fetchInfo(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException {
		return fileManager.fetchInfo(path, options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getChild(java.lang.String)
	 */
	@Override
	public IFileStore getChild(String name) {
		return new VirtualFile(fileManager, baseURI, path.append(name));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getName()
	 */
	@Override
	public String getName() {
		return (path.segmentCount() == 0) ? path.toPortableString() : path.lastSegment();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getParent()
	 */
	@Override
	public IFileStore getParent() {
		if (path.isRoot()) {
			return null;
		}
		return new VirtualFile(fileManager, baseURI, path.removeLastSegments(1));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#isParentOf(org.eclipse.core.filesystem.IFileStore)
	 */
	@Override
	public boolean isParentOf(IFileStore other) {
		if (other instanceof VirtualFile) {
		    VirtualFile otherFile = (VirtualFile) other;
            return baseURI.equals(otherFile.baseURI)
                    && ((path.isRoot() && !otherFile.path.isRoot()) || (path
                            .matchingFirstSegments(otherFile.path) == path.segmentCount() && path
                            .segmentCount() <= otherFile.path.segmentCount()));
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#openInputStream(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException {
		return fileManager.openInputStream(path, options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#openOutputStream(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException {
		return fileManager.openOutputStream(path, options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#delete(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void delete(int options, IProgressMonitor monitor) throws CoreException {
		fileManager.delete(path, options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#mkdir(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException {
		fileManager.mkdir(path, options, monitor);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#copy(org.eclipse.core.filesystem.IFileStore, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void copy(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException {
		if (destination instanceof VirtualFile) {
			if (((VirtualFile) destination).toCanonicalURI().equals(toCanonicalURI())) {
				//nothing to do
				return;
			}
			//TODO: max - special handling required (toLocalFile first)?
		}
		super.copy(destination, options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#move(org.eclipse.core.filesystem.IFileStore, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void move(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException {
		if (destination instanceof VirtualFile) {
			if (((VirtualFile) destination).toCanonicalURI().equals(toCanonicalURI())) {
				//nothing to do
				return;
			}
			if (((VirtualFile) destination).fileManager == fileManager) { // $codepro.audit.disable useEquals
				fileManager.move(path, ((VirtualFile) destination).path, options, monitor);
				return;
			}

		}
		super.move(destination, options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#putInfo(org.eclipse.core.filesystem.IFileInfo, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void putInfo(IFileInfo info, int options, IProgressMonitor monitor) throws CoreException {
	    options &= ~EFS.SET_ATTRIBUTES;
		fileManager.putInfo(path, info, options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#toURI()
	 */
	@Override
	public URI toURI() {
		return baseURI.resolve(URLEncoder.encode(path.toPortableString(), null, null));
	}
	
	public URI toCanonicalURI() {
		return fileManager.getCanonicalURI(path);
	}
	
	public IFileTree fetchFileTree(IFileTreeVisitor visitor, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			FileTree fileTree = new FileTree(this);
			buildFileTree(fileTree, this, visitor, new InfiniteProgressMonitor(monitor));
			return fileTree;
			} finally {
				monitor.done();
			}
	}

	public String toString() {
	    return toCanonicalURI().toString();
	}

	private static void buildFileTree(FileTree fileTree, VirtualFile parent, IFileTreeVisitor visitor, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(MessageFormat.format(Messages.VirtualFile_ListingDirectory, parent.path), 20);
		IFileInfo[] infos = parent.fileManager.childInfos(parent.path, IExtendedFileStore.DETAILED, monitor);
		List<IFileStore> stores = new ArrayList<IFileStore>();
		List<IFileStore> dirs = new ArrayList<IFileStore>();
		for (IFileInfo fileInfo : infos) {
			IFileStore store = parent.getChild(fileInfo.getName());
			if (visitor != null && !visitor.include(store)) {
				continue;
			}
			stores.add(store);
			if (fileInfo.isDirectory()) {
				dirs.add(store);
			}
		}
		fileTree.addChildren(parent, stores.toArray(new IFileStore[stores.size()]), infos);
		monitor.worked(1);
		for (IFileStore store : dirs) {
			buildFileTree(fileTree, (VirtualFile) store, visitor, monitor);
		}
	}
}
