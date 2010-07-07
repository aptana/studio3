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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.internal.filesystem.Messages;
import org.eclipse.core.internal.filesystem.Policy;
import org.eclipse.core.internal.filesystem.local.LocalFile;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.preferences.CloakingUtils;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
/* package */ class WorkspaceFile extends FileStore {
		
	private static final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
	
	private IResource resource;
	private final IPath path;
	private IFileStore localFileStore;

	/**
	 * 
	 */
	public WorkspaceFile(IResource resource) {
		this(resource, resource.getFullPath());
		
	}

	/**
	 * 
	 */
	public WorkspaceFile(IPath path) {
		this(null, path);
	}

	/**
	 * 
	 */
	private WorkspaceFile(IResource resource, IPath path) {
		this.resource = resource;
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (IResource.class == adapter) {
			try {
				ensureResource();
			} catch (CoreException e) {
			}
			return resource;
		}
		return super.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#childNames(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public String[] childNames(int options, IProgressMonitor monitor) throws CoreException {
		ensureResource();
		if (resource instanceof IContainer) {
			IContainer container = (IContainer) resource;
			if (!container.isSynchronized(IResource.DEPTH_ONE)) {
				container.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
			}
			final List<String> childNames = new ArrayList<String>();
			final boolean[] skipSelf = new boolean[] { true };
			container.accept(new IResourceProxyVisitor() {
				public boolean visit(IResourceProxy proxy) throws CoreException {
					if (skipSelf[0]) {
						skipSelf[0] = false;
						return true;
					}
					childNames.add(proxy.getName());
					return false;
				}
			}, IContainer.INCLUDE_HIDDEN);
			return childNames.toArray(new String[childNames.size()]);
		}
		return EMPTY_STRING_ARRAY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#fetchInfo(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException {
		ensureLocalFileStore();
		if (localFileStore != null) {
			return localFileStore.fetchInfo(options, monitor);
		}
		FileInfo info = new FileInfo(path.lastSegment());
		info.setExists(false);
		return info;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getChild(java.lang.String)
	 */
	@Override
	public IFileStore getChild(String name) {
		return new WorkspaceFile(path.append(name));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getName()
	 */
	@Override
	public String getName() {
		return path.lastSegment();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getParent()
	 */
	@Override
	public IFileStore getParent() {
		return new WorkspaceFile(path.removeLastSegments(1));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#openInputStream(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException {
		ensureLocalFileStore();
		if (localFileStore != null) {
			return localFileStore.openInputStream(options, monitor);
		}
		Policy.error(EFS.ERROR_READ, NLS.bind(Messages.fileNotFound, path));
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#toURI()
	 */
	@Override
	public URI toURI() {
		try {
			return new URI(WorkspaceFileSystem.SCHEME_WORKSPACE, path.toPortableString(), null);
		} catch (URISyntaxException e) {
			CoreIOPlugin.log(new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID, e.getLocalizedMessage(), e));
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#copy(org.eclipse.core.filesystem.IFileStore, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void copy(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException {
	    if (CloakingUtils.isFileCloaked(this)) {
	        // this file is cloaked from transferring
	        return;
	    }
		ensureLocalFileStore();
		if (localFileStore != null) {
			localFileStore.copy(destination, options, monitor);
		} else {
			super.copy(destination, options, monitor);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#delete(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void delete(int options, IProgressMonitor monitor) throws CoreException {
		ensureLocalFileStore();
		if (localFileStore != null) {
			localFileStore.delete(options, monitor);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getFileStore(org.eclipse.core.runtime.IPath)
	 */
	@Override
	public IFileStore getFileStore(IPath path) {
		return new WorkspaceFile(this.path.append(path));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getFileSystem()
	 */
	@Override
	public IFileSystem getFileSystem() {
		return WorkspaceFileSystem.getInstance();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WorkspaceFile)) {
			return false;
		}
		return path.equals(((WorkspaceFile) obj).path);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#hashCode()
	 */
	@Override
	public int hashCode() {
		return path.hashCode();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#mkdir(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException {
		ensureLocalFileStore(true);
		if (localFileStore != null) {
			try {
				localFileStore.mkdir(options, monitor);
			} finally {
				localFileStore = null;
			}
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#move(org.eclipse.core.filesystem.IFileStore, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void move(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException {
		if (!(destination instanceof WorkspaceFile)) {
			ensureLocalFileStore();
			if (localFileStore != null) {
				localFileStore.move(destination, options, monitor);
				return;
			}
			Policy.error(EFS.ERROR_NOT_EXISTS, NLS.bind(Messages.fileNotFound, path));
		}
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(NLS.bind(Messages.moving, destination.toString()), 100);
		WorkspaceFile destinationFile = (WorkspaceFile) destination;
		try {
			ensureResource();
			if (resource == null) {
				Policy.error(EFS.ERROR_NOT_EXISTS, NLS.bind(Messages.fileNotFound, path));
			}
			IResource destinationResource = (IResource) destinationFile.getAdapter(IResource.class);
			if (destinationResource == null) {
			    if (resource instanceof IContainer) {
			        destinationResource = workspaceRoot.getFolder(destinationFile.path);
			    } else {
			        destinationResource = workspaceRoot.getFile(destinationFile.path);
			    }
			}
			boolean sourceEqualsDest = resource.equals(destinationResource);
			boolean overwrite = (options & EFS.OVERWRITE) != 0;
			if (!sourceEqualsDest && !overwrite && destinationResource.exists()) {
				Policy.error(EFS.ERROR_EXISTS,  NLS.bind(Messages.fileExists, destinationResource.getFullPath()));
			}
			try {
				resource.move(destinationResource.getFullPath(), true, Policy.subMonitorFor(monitor, 100));
			} catch (CoreException e) {
				Policy.error(EFS.ERROR_WRITE, NLS.bind(Messages.failedMove, toString(), destination.toString()), e);
			}
		} finally {
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#putInfo(org.eclipse.core.filesystem.IFileInfo, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void putInfo(IFileInfo info, int options, IProgressMonitor monitor) throws CoreException {
		ensureLocalFileStore();
		if (localFileStore != null) {
			localFileStore.putInfo(info, options, monitor);
		} else {
			Policy.error(EFS.ERROR_NOT_EXISTS, NLS.bind(Messages.fileNotFound, path));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#toLocalFile(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public File toLocalFile(int options, IProgressMonitor monitor) throws CoreException {
		ensureLocalFileStore();
		if (localFileStore != null) {
			return localFileStore.toLocalFile(options, monitor);
		}
		return new LocalFile(workspaceRoot.getFile(path).getLocation().toFile()).toLocalFile(options, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#openOutputStream(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException {
		ensureLocalFileStore(true);
		if (localFileStore != null) {
			return localFileStore.openOutputStream(options, monitor);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#toString()
	 */
	@Override
	public String toString() {
		return path.toString();
	}
	
	private void ensureResource() throws CoreException {
		if (resource != null && (
				!resource.isSynchronized(IResource.DEPTH_ZERO)
				|| !resource.exists())) {
			resource = null;
			localFileStore = null;
		}
		if (resource == null) {
			IResource res = workspaceRoot;
			for (String name : path.segments()) {
				if (res instanceof IContainer) {
					IContainer container = (IContainer) res;
					if (!container.isSynchronized(IResource.DEPTH_ONE)) {
						container.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
					}
					res = container.findMember(name);
				} else {
					res = null;
					break;
				}
			}
			resource = res;
		}		
	}

	private void ensureLocalFileStore() throws CoreException {
		ensureLocalFileStore(false);
	}

	private void ensureLocalFileStore(boolean force) throws CoreException {
		ensureResource();
		if (localFileStore == null) {
			if (resource != null && resource.exists()) {
				localFileStore = new LocalFile(resource.getLocation().toFile());
			} else if (force) {
				IResource parent = workspaceRoot;
				IPath relativePath = null;
				for (int i = 0; i < path.segmentCount(); ++i) {
					if (parent instanceof IContainer) {
						IResource member = ((IContainer) parent).findMember(path.segment(i));
						if (member != null) {
							parent = member;
						} else {
							relativePath = path.removeFirstSegments(i);
							break;
						}
					} else {
						parent = null;
						break;
					}
				}
				if (parent != null & relativePath != null) {
					localFileStore = new LocalFile(parent.getLocation().toFile()).getFileStore(relativePath);
				}
				
			}
		}
	}

	public static IFileStore fromLocalFile(File file) {
		IResource resource = null;
		if (file.isDirectory()) {
			resource = workspaceRoot.getContainerForLocation(Path.fromOSString(file.getAbsolutePath()));
		} else if (file.isFile()) {
			resource = workspaceRoot.getFileForLocation(Path.fromOSString(file.getAbsolutePath()));			
		}
		if (resource != null) {
			return new WorkspaceFile(resource);
		}
		return null;
	}
}
