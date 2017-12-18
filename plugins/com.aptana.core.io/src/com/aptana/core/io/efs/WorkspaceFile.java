/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable closeWhereCreated
// $codepro.audit.disable unnecessaryExceptions
// $codepro.audit.disable questionableAssignment

package com.aptana.core.io.efs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.IFileTree;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.internal.filesystem.Messages;
import org.eclipse.core.internal.filesystem.local.LocalFile;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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

import com.aptana.core.io.vfs.IFileTreeVisitor;
import com.aptana.core.io.vfs.Policy;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.InfiniteProgressMonitor;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
/* package */class WorkspaceFile extends FileStore
{

	private static final byte[] EMPTY_ARRAY = new byte[0];

	private static final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

	private IResource resource;
	private final IPath path;
	private LocalFile localFileStore;

	/**
	 * 
	 */
	private WorkspaceFile(IResource resource)
	{
		this(resource, resource.getFullPath());
	}

	/**
	 * 
	 */
	protected WorkspaceFile(IPath path)
	{
		this(null, path);
	}

	/**
	 * 
	 */
	private WorkspaceFile(IResource resource, IPath path)
	{
		this.resource = resource;
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (IResource.class.equals(adapter))
		{
			try
			{
				ensureResource();
			}
			catch (CoreException e)
			{
				IdeLog.logWarning(CoreIOPlugin.getDefault(), e);
			}
			return resource;
		}
		return super.getAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#childNames(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
	{
		ensureResource();
		if (resource instanceof IContainer)
		{
			IContainer container = (IContainer) resource;
			if (!container.isSynchronized(IResource.DEPTH_ONE))
			{
				container.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
			}
			final List<String> childNames = new ArrayList<String>();
			final boolean[] skipSelf = new boolean[] { true };
			container.accept(new IResourceProxyVisitor()
			{
				public boolean visit(IResourceProxy proxy) throws CoreException
				{
					if (skipSelf[0])
					{
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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#fetchInfo(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
	{
		ensureLocalFileStore();
		if (localFileStore != null)
		{
			FileInfo fileInfo = (FileInfo) localFileStore.fetchInfo(options, monitor);
			if (path.isRoot())
			{
				fileInfo.setName(path.toPortableString());
			}
			return fileInfo;
		}
		FileInfo info = new FileInfo(path.lastSegment());
		info.setExists(false);
		return info;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getChild(java.lang.String)
	 */
	@Override
	public IFileStore getChild(String name)
	{
		return new WorkspaceFile(path.append(name));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getName()
	 */
	@Override
	public String getName()
	{
		return path.lastSegment();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getParent()
	 */
	@Override
	public IFileStore getParent()
	{
		if (path.equals(Path.ROOT))
		{
			return null;
		}
		return new WorkspaceFile(path.removeLastSegments(1));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#openInputStream(int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
	{
		ensureResource();
		if (resource instanceof IFile)
		{
			return ((IFile) resource).getContents(true);
		}
		org.eclipse.core.internal.filesystem.Policy.error(EFS.ERROR_READ, NLS.bind(Messages.fileNotFound, path),
				new FileNotFoundException(path.toPortableString()));
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#toURI()
	 */
	@Override
	public URI toURI()
	{
		try
		{
			return new URI(WorkspaceFileSystem.SCHEME_WORKSPACE, null, path.toPortableString(), null);
		}
		catch (URISyntaxException e)
		{
			IdeLog.logError(CoreIOPlugin.getDefault(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#copy(org.eclipse.core. filesystem.IFileStore, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void copy(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException
	{
		ensureResource();
		if (resource != null && destination instanceof WorkspaceFile)
		{
			resource.copy(((WorkspaceFile) destination).path, IResource.FORCE | IResource.SHALLOW, monitor);
		}
		else
		{
			super.copy(destination, options, monitor);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#delete(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void delete(int options, IProgressMonitor monitor) throws CoreException
	{
		ensureResource();
		if (resource != null)
		{
			resource.delete(IResource.FORCE | IResource.KEEP_HISTORY, monitor);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getFileStore(org.eclipse .core.runtime.IPath)
	 */
	@Override
	public IFileStore getFileStore(IPath path)
	{
		return new WorkspaceFile(this.path.append(path));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#getFileSystem()
	 */
	@Override
	public IFileSystem getFileSystem()
	{
		return WorkspaceFileSystem.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (!(obj instanceof WorkspaceFile))
		{
			return false;
		}
		return path.equals(((WorkspaceFile) obj).path);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return path.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#mkdir(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException
	{
		ensureLocalFileStore(IFolder.class);
		if (resource != null && !resource.exists())
		{
			monitor = Policy.monitorFor(monitor);
			monitor.beginTask(MessageFormat.format("Creating folder {0}", path.lastSegment()), 100); //$NON-NLS-1$
			try
			{
				if ((options & EFS.SHALLOW) == 0)
				{
					createParentsRecursive(resource, Policy.subMonitorFor(monitor, 80));
				}
				else
				{
					Policy.subMonitorFor(monitor, 80).done();
				}
				((IFolder) resource).create(IResource.FORCE, true, Policy.subMonitorFor(monitor, 20));
			}
			catch (CoreException e)
			{
				fileNotFoundError(e, path);
			}
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#move(org.eclipse.core. filesystem.IFileStore, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void move(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException
	{
		if (!(destination instanceof WorkspaceFile))
		{
			ensureResource();
			if (resource != null && resource.exists())
			{
				super.move(destination, options, monitor);
				return;
			}
			org.eclipse.core.internal.filesystem.Policy.error(EFS.ERROR_NOT_EXISTS,
					NLS.bind(Messages.fileNotFound, path), new FileNotFoundException(path.toPortableString()));
		}
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(NLS.bind(Messages.moving, destination.toString()), 100);
		WorkspaceFile destinationFile = (WorkspaceFile) destination;
		try
		{
			ensureResource();
			if (resource == null)
			{
				org.eclipse.core.internal.filesystem.Policy.error(EFS.ERROR_NOT_EXISTS,
						NLS.bind(Messages.fileNotFound, path), new FileNotFoundException(path.toPortableString()));
			}
			IResource destinationResource = destinationFile.ensureResource();
			if (destinationResource == null)
			{
				if (resource instanceof IContainer)
				{
					destinationResource = workspaceRoot.getFolder(destinationFile.path);
				}
				else
				{
					destinationResource = workspaceRoot.getFile(destinationFile.path);
				}
			}
			boolean sourceEqualsDest = resource.equals(destinationResource);
			boolean overwrite = (options & EFS.OVERWRITE) != 0;
			if (!sourceEqualsDest && !overwrite && destinationResource.exists())
			{
				org.eclipse.core.internal.filesystem.Policy.error(EFS.ERROR_EXISTS,
						NLS.bind(Messages.fileExists, destinationResource.getFullPath()),
						new FileNotFoundException(destinationFile.path.toPortableString()));
			}
			try
			{
				if (destinationResource.exists())
				{
					destinationResource.delete(IResource.FORCE, Policy.subMonitorFor(monitor, 20));
				}
			}
			catch (CoreException e)
			{
				org.eclipse.core.internal.filesystem.Policy.error(EFS.ERROR_DELETE,
						NLS.bind(Messages.couldnotDelete, toString(), destination.toString()),
						new FileNotFoundException(destinationFile.path.toPortableString()));
			}
			if (!destinationResource.getParent().exists())
			{
				org.eclipse.core.internal.filesystem.Policy.error(EFS.ERROR_NOT_EXISTS,
						NLS.bind(Messages.fileNotFound, toString(), destination.toString()),
						new FileNotFoundException(destinationFile.path.toPortableString()));
			}
			try
			{
				resource.move(destinationResource.getFullPath(), true, Policy.subMonitorFor(monitor, 80));
			}
			catch (CoreException e)
			{
				org.eclipse.core.internal.filesystem.Policy.error(EFS.ERROR_WRITE,
						NLS.bind(Messages.failedMove, toString(), destination.toString()), e);
			}
		}
		finally
		{
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#putInfo(org.eclipse.core .filesystem.IFileInfo, int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void putInfo(IFileInfo info, int options, IProgressMonitor monitor) throws CoreException
	{
		ensureLocalFileStore();
		if (localFileStore != null)
		{
			localFileStore.putInfo(info, options, monitor);
		}
		else
		{
			org.eclipse.core.internal.filesystem.Policy.error(EFS.ERROR_NOT_EXISTS,
					NLS.bind(Messages.fileNotFound, path));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#toLocalFile(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public File toLocalFile(int options, IProgressMonitor monitor) throws CoreException
	{
		ensureLocalFileStore();
		if (localFileStore != null)
		{
			return localFileStore.toLocalFile(options, monitor);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#openOutputStream(int,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException
	{
		ensureLocalFileStore(IFile.class);
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(StringUtil.EMPTY, 100);
		if (localFileStore == null)
		{
			try
			{
				((IFile) resource).create(new ByteArrayInputStream(EMPTY_ARRAY), IResource.FORCE,
						Policy.subMonitorFor(monitor, 50));
			}
			catch (CoreException e)
			{
				fileNotFoundError(e, path);
			}
			ensureLocalFileStore();
		}
		if (localFileStore != null)
		{
			return localFileStore.openOutputStream(options, Policy.subMonitorFor(monitor, 50));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileStore#toString()
	 */
	@Override
	public String toString()
	{
		return path.toString();
	}

	private IResource ensureResource() throws CoreException
	{
		return ensureResource(null);
	}

	private IResource ensureResource(Class<? extends IResource> resourceClass) throws CoreException
	{
		if (resource != null && (!resource.isSynchronized(IResource.DEPTH_ZERO) || !resource.exists()))
		{
			resource = null;
			localFileStore = null;
		}
		if (resource == null)
		{
			IContainer container = workspaceRoot;
			if (path.segmentCount() > 2)
			{
				container = workspaceRoot.getFolder(path.removeLastSegments(1));
			}
			else if (path.segmentCount() == 2)
			{
				container = workspaceRoot.getProject(path.segment(0));
			}
			if (path.isRoot())
			{
				resource = workspaceRoot;
			}
			else
			{
				resource = container.findMember(path.lastSegment());
			}
			if (resource == null)
			{
				if (IFile.class.equals(resourceClass))
				{
					resource = workspaceRoot.getFile(path);
				}
				else if (IFolder.class.equals(resourceClass))
				{
					resource = workspaceRoot.getFolder(path);
				}
			}
			if (resourceClass != null && !resourceClass.isInstance(resource))
			{
				resource = null;
				org.eclipse.core.internal.filesystem.Policy.error(EFS.ERROR_WRONG_TYPE,
						NLS.bind(Messages.failedCreateWrongType, path));
			}
		}
		return resource;
	}

	private LocalFile ensureLocalFileStore() throws CoreException
	{
		return ensureLocalFileStore(null);
	}

	private LocalFile ensureLocalFileStore(Class<? extends IResource> resourceClass) throws CoreException
	{
		ensureResource(resourceClass);
		if (localFileStore == null)
		{
			if (resource != null && resource.exists() && resource.getLocation() != null)
			{
				localFileStore = new LocalFile(resource.getLocation().toFile());
			}
		}
		return localFileStore;
	}

	private static void createParentsRecursive(IResource resource, IProgressMonitor monitor) throws CoreException
	{
		if (resource == null)
		{
			return;
		}
		IContainer parent = resource.getParent();
		if (parent.exists())
		{
			return;
		}
		monitor.beginTask(StringUtil.EMPTY, 100);
		createParentsRecursive(parent, Policy.subMonitorFor(monitor, 80));
		if (parent instanceof IFolder)
		{
			((IFolder) parent).create(IResource.FORCE, true, Policy.subMonitorFor(monitor, 20));
		}
	}

	private static void fileNotFoundError(CoreException cause, IPath path) throws CoreException
	{
		IStatus status = cause.getStatus();
		throw new CoreException(new Status(status.getSeverity(), status.getPlugin(), status.getCode(),
				status.getMessage(), new FileNotFoundException(path.toPortableString())));
	}

	public static IFileStore fromLocalFile(File file)
	{
		IResource resource = null;
		if (file.isDirectory())
		{
			resource = workspaceRoot.getContainerForLocation(Path.fromOSString(file.getAbsolutePath()));
		}
		else if (file.isFile())
		{
			resource = workspaceRoot.getFileForLocation(Path.fromOSString(file.getAbsolutePath()));
		}
		if (resource != null)
		{
			return new WorkspaceFile(resource);
		}
		return null;
	}

	public IFileTree fetchFileTree(IFileTreeVisitor visitor, IProgressMonitor monitor) throws CoreException
	{
		monitor = Policy.monitorFor(monitor);
		try
		{
			FileTree fileTree = new FileTree(this);
			buildFileTree(fileTree, this, visitor, new InfiniteProgressMonitor(monitor));
			return fileTree;
		}
		finally
		{
			monitor.done();
		}
	}

	private static void buildFileTree(FileTree fileTree, WorkspaceFile parent, IFileTreeVisitor visitor,
			IProgressMonitor monitor) throws CoreException
	{
		monitor.beginTask(MessageFormat.format("Listing directory {0}", parent.path), 20); //$NON-NLS-1$
		IFileInfo[] infos = parent.childInfos(EFS.NONE, monitor);
		List<IFileStore> stores = new ArrayList<IFileStore>();
		List<IFileStore> dirs = new ArrayList<IFileStore>();
		for (IFileInfo fileInfo : infos)
		{
			IFileStore store = parent.getChild(fileInfo.getName());
			if (visitor != null && !visitor.include(store))
			{
				continue;
			}
			stores.add(store);
			if (fileInfo.isDirectory())
			{
				dirs.add(store);
			}
		}
		fileTree.addChildren(parent, stores.toArray(new IFileStore[stores.size()]), infos);
		monitor.worked(1);
		for (IFileStore store : dirs)
		{
			buildFileTree(fileTree, (WorkspaceFile) store, visitor, monitor);
		}
	}
}
