/*******************************************************************************
 * Copyright (C) 2008, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.internal.core.storage;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.provider.FileHistoryProvider;

/**
 * A {@link FileHistoryProvider} for Git. This class has methods for retrieving specific versions of a tracked resource.
 */
public class GitFileHistoryProvider extends FileHistoryProvider
{
	public IFileHistory getFileHistoryFor(IResource resource, int flags, IProgressMonitor monitor)
	{
		return new GitFileHistory(resource, flags, monitor);
	}

	public IFileRevision getWorkspaceFileRevision(IResource resource)
	{
		return new WorkspaceFileRevision(resource);
	}

	public IFileHistory getFileHistoryFor(IFileStore store, int flags, IProgressMonitor monitor)
	{
		// TODO: implement getFileHistoryFor(IFileStore ...)
		return null;
	}
}
