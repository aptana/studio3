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
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.team.core.history.IFileRevision;

import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitExecutable;

/**
 * An {@link IFileRevision} for a version of a specified resource in the specified commit (revision).
 */
class CommitFileRevision extends GitFileRevision
{

	private GitCommit commit;

	public CommitFileRevision(GitCommit gitCommit, String filename)
	{
		super(filename);
		this.commit = gitCommit;
	}

	@Override
	public IStorage getStorage(IProgressMonitor monitor) throws CoreException
	{
		final GitFileRevision self = this;
		final String output = GitExecutable.instance().outputForCommand(commit.repository().workingDirectory(), "show",
				commit.sha());
		return new IStorage()
		{

			@Override
			public Object getAdapter(Class adapter)
			{
				return null;
			}

			@Override
			public boolean isReadOnly()
			{
				return true;
			}

			@Override
			public String getName()
			{
				return self.getName();
			}

			@Override
			public IPath getFullPath()
			{
				return new Path(self.path);
			}

			@Override
			public InputStream getContents() throws CoreException
			{
				return new ByteArrayInputStream(output.getBytes());
			}
		};
	}

	@Override
	public String getAuthor()
	{
		return commit.getAuthor();
	}

	@Override
	public String getComment()
	{
		return commit.getComment();
	}

	@Override
	public long getTimestamp()
	{
		return commit.getTimestamp();
	}

}
