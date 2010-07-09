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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.team.core.history.IFileRevision;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitExecutable;

/**
 * An {@link IFileRevision} for a version of a specified resource in the specified commit (revision).
 */
public class CommitFileRevision extends GitFileRevision
{

	private GitCommit commit;

	public CommitFileRevision(GitCommit gitCommit, String filename)
	{
		super(filename);
		this.commit = gitCommit;
	}

	public IStorage getStorage(IProgressMonitor monitor) throws CoreException
	{
		final GitFileRevision self = this;
		return new IStorage()
		{

			@SuppressWarnings("rawtypes")
			public Object getAdapter(Class adapter)
			{
				return null;
			}

			public boolean isReadOnly()
			{
				return true;
			}

			public String getName()
			{
				return self.getName();
			}

			public IPath getFullPath()
			{
				return new Path(path);
			}

			public InputStream getContents() throws CoreException
			{
				if (commit == null)
				{
					return new ByteArrayInputStream(new byte[0]);
				}
				try
				{
					Process p = GitExecutable.instance().run(commit.repository().workingDirectory(), "show", //$NON-NLS-1$
							commit.sha() + ":" + path); //$NON-NLS-1$
					return p.getInputStream();
				}
				catch (IOException e)
				{
					throw new CoreException(new Status(IStatus.ERROR, GitPlugin.getPluginId(), e.getMessage(), e));
				}

			}
		};
	}

	@Override
	public String getAuthor()
	{
		if (commit == null)
		{
			return null;
		}
		return commit.getAuthor();
	}

	@Override
	public String getComment()
	{
		if (commit == null)
		{
			return null;
		}
		return commit.getComment();
	}

	@Override
	public long getTimestamp()
	{
		if (commit == null)
		{
			return -1L;
		}
		return commit.getTimestamp();
	}

	@Override
	public String getContentIdentifier()
	{
		if (commit == null)
		{
			return null;
		}
		return commit.sha();
	}

	public boolean isDescendantOf(IFileRevision revision)
	{
		if (!(revision instanceof CommitFileRevision))
			return false;
		if (commit == null)
			return false;
		if (!commit.hasParent())
			return false;
		CommitFileRevision other = (CommitFileRevision) revision;
		return commit.parents().contains(other.commit.sha());
	}
}
