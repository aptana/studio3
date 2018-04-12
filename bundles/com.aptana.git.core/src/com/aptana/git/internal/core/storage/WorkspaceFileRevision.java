/*******************************************************************************
 * Copyright (C) 2007, Robin Rosenberg <me@lathund.dewire.com>
 * Copyright (C) 2006, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.internal.core.storage;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.history.IFileRevision;

/** An {@link IFileRevision} for the current version in the workspace. */
class WorkspaceFileRevision extends GitFileRevision
{

	/** Content identifier for the working copy. */
	private static final String WORKSPACE = "Workspace"; //$NON-NLS-1$

	private final IResource rsrc;

	WorkspaceFileRevision(final IResource resource)
	{
		// FIXME We need the git repo relative path, not just the last segment!
		super(resource.getName());
		rsrc = resource;
	}

	public IStorage getStorage(IProgressMonitor monitor)
	{
		return rsrc instanceof IStorage ? (IStorage) rsrc : null;
	}

	public boolean isPropertyMissing()
	{
		return false;
	}

	public IFileRevision withAllProperties(IProgressMonitor monitor)
	{
		return null;
	}

	public String getAuthor()
	{
		return ""; //$NON-NLS-1$
	}

	public long getTimestamp()
	{
		return -1;
	}

	public String getComment()
	{
		return ""; //$NON-NLS-1$
	}

	public String getContentIdentifier()
	{
		return WORKSPACE;
	}
}
