/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.storage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.provider.FileHistory;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;
import com.aptana.git.core.model.IGitRepositoryManager;

public class GitFileHistory extends FileHistory
{

	private static final IFileRevision[] NO_FILE_REVISIONS = new IFileRevision[0];
	private static final CommitFileRevision[] NO_COMMIT_FILE_REVISIONS = new CommitFileRevision[0];
	private IResource resource;
	private final CommitFileRevision[] revisions;

	public GitFileHistory(IResource resource, int flags, IProgressMonitor monitor)
	{
		this.resource = resource;
		this.revisions = buildRevisions(flags, monitor);
	}

	private CommitFileRevision[] buildRevisions(int flags, IProgressMonitor monitor)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		try
		{
			if (resource == null || resource.getProject() == null)
			{
				return NO_COMMIT_FILE_REVISIONS;
			}
			GitRepository repo = getGitRepositoryManager().getAttached(this.resource.getProject());
			if (repo == null)
			{
				return NO_COMMIT_FILE_REVISIONS;
			}
			// Need the repo relative path
			IPath resourcePath = repo.relativePath(resource);
			List<IFileRevision> revisions = new ArrayList<IFileRevision>();
			GitRevList list = new GitRevList(repo);
			int max = -1;
			if ((flags & IFileHistoryProvider.SINGLE_REVISION) == IFileHistoryProvider.SINGLE_REVISION)
			{
				max = 1;
			}
			list.walkRevisionListWithSpecifier(new GitRevSpecifier(resourcePath.toOSString()), max,
					subMonitor.newChild(95));
			List<GitCommit> commits = list.getCommits();
			for (GitCommit gitCommit : commits)
			{
				revisions.add(new CommitFileRevision(gitCommit, resource.getProjectRelativePath()));
			}
			return revisions.toArray(new CommitFileRevision[revisions.size()]);
		}
		finally
		{
			subMonitor.done();
		}
	}

	private IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	public IFileRevision[] getContributors(IFileRevision revision)
	{
		if (!(revision instanceof CommitFileRevision))
		{
			return NO_FILE_REVISIONS;
		}
		CommitFileRevision arg = (CommitFileRevision) revision;
		List<IFileRevision> targets = new ArrayList<IFileRevision>();
		if (revisions != null)
		{
			for (CommitFileRevision aRevision : revisions)
			{
				if (arg.isDescendantOf(aRevision))
				{
					targets.add(aRevision);
				}
			}
		}
		return targets.toArray(new IFileRevision[targets.size()]);
	}

	public IFileRevision getFileRevision(String id)
	{
		if (revisions != null)
		{
			for (IFileRevision revision : revisions)
			{
				if (revision.getContentIdentifier().equals(id))
				{
					return revision;
				}
			}
		}
		return null;
	}

	public IFileRevision[] getFileRevisions()
	{
		final IFileRevision[] r = new IFileRevision[revisions.length];
		System.arraycopy(revisions, 0, r, 0, r.length);
		return r;
	}

	public IFileRevision[] getTargets(IFileRevision revision)
	{
		if (!(revision instanceof CommitFileRevision))
		{
			return NO_FILE_REVISIONS;
		}
		List<IFileRevision> targets = new ArrayList<IFileRevision>();
		if (revisions != null)
		{
			for (CommitFileRevision aRevision : revisions)
			{
				if (aRevision.isDescendantOf(revision))
				{
					targets.add(aRevision);
				}
			}
		}
		return targets.toArray(new IFileRevision[targets.size()]);
	}

}
