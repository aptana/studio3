/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.ide.core.io;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.epl.IMemento;
import com.aptana.core.io.efs.WorkspaceFileSystem;

/**
 * @author Max Stepanov
 */
public final class WorkspaceConnectionPoint extends ConnectionPoint
{

	public static final String TYPE = "workspace"; //$NON-NLS-1$

	private static final String ELEMENT_PATH = "path"; //$NON-NLS-1$

	private static IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

	private IPath path; /* workspace-relative path */

	/**
	 * Default constructor
	 */
	public WorkspaceConnectionPoint()
	{
		super(TYPE);
	}

	/**
	 * 
	 */
	/* package */WorkspaceConnectionPoint(IContainer resource)
	{
		super(TYPE);
		this.path = resource.getFullPath();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		if (IResource.class.isAssignableFrom(adapter))
		{
			IContainer resource = getResource();
			if (resource != null)
			{
				Object result = resource.getAdapter(adapter);
				if (result != null)
				{
					return result;
				}
			}
		}
		return super.getAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#getRootURI()
	 */
	@Override
	public URI getRootURI()
	{
		return WorkspaceFileSystem.getInstance().getStore(path).toURI();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#getRoot()
	 */
	@Override
	public IFileStore getRoot() throws CoreException
	{
		return WorkspaceFileSystem.getInstance().getStore(path);
	}

	/**
	 * @return the resource
	 */
	public IContainer getResource()
	{
		IResource resource = workspaceRoot.findMember(path);
		if (resource instanceof IContainer)
		{
			return (IContainer) resource;
		}
		return null;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void setResource(IContainer resource)
	{
		this.path = resource.getFullPath();
	}

	public IPath getPath()
	{
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#loadState(com.aptana.ide.core.epl.IMemento)
	 */
	@Override
	protected void loadState(IMemento memento)
	{
		super.loadState(memento);
		IMemento child = memento.getChild(ELEMENT_PATH);
		if (child != null)
		{
			path = Path.fromPortableString(child.getTextData());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ConnectionPoint#saveState(com.aptana.ide.core.epl.IMemento)
	 */
	@Override
	protected void saveState(IMemento memento)
	{
		super.saveState(memento);
		memento.createChild(ELEMENT_PATH).putTextData(path.toPortableString());
	}
}
