/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.core.internal.refresh.RefreshManager;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.model.GitRepository.ReadWrite;

/**
 * Represents a commit in the repo.
 * 
 * @author cwilliams
 */
public class GitCommit
{

	private GitRepository repository;
	private String sha;
	private String subject;
	private long timestamp;
	private String author;
	private String authorEmail;
	private List<String> parentShas;
	private String comment;
	private List<Diff> diffs;

	public GitCommit(GitRepository repository, String sha)
	{
		this.repository = repository;
		this.sha = sha;
	}

	public List<String> parents()
	{
		return parentShas;
	}

	public Date date()
	{
		return new Date(timestamp);
	}

	public String sha()
	{
		return sha;
	}

	public GitRepository repository()
	{
		return repository;
	}

	void setSubject(String subject)
	{
		this.subject = subject;
	}

	void setAuthor(String author)
	{
		this.author = author;
	}

	void setTimestamp(long time)
	{
		this.timestamp = time;
	}

	void setParents(List<String> parents)
	{
		this.parentShas = new ArrayList<String>(parents);
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public String getSubject()
	{
		return subject;
	}

	public String getAuthor()
	{
		return author;
	}

	public String getComment()
	{
		return comment;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("commit ").append(sha).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
		builder.append(getComment());
		return builder.toString();
	}

	void setComment(String comment)
	{
		this.comment = comment;
	}

	public synchronized List<Diff> getDiff()
	{
		if (diffs == null)
		{
			diffs = Diff.create(this);
		}
		return diffs;
	}

	private List<IResource> affectedResources()
	{
		IPath wd = repository.workingDirectory();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IContainer container = root.getContainerForLocation(wd);
		if (container == null)
		{
			return Collections.emptyList();
		}
		return CollectionsUtil.map(affectedPaths(), new IMap<IPath, IResource>()
		{

			public IResource map(IPath item)
			{
				return container.findMember(item);
			}
		});
	}

	public GitCommit getFirstParent()
	{
		if (CollectionsUtil.isEmpty(parents()))
		{
			return null;
		}
		return new GitCommit(repository, parentShas.get(0));
	}

	public boolean hasParent()
	{
		return !CollectionsUtil.isEmpty(parentShas);
	}

	public int parentCount()
	{
		if (CollectionsUtil.isEmpty(parentShas))
		{
			return 0;
		}
		return parentShas.size();
	}

	public Collection<GitRef> getRefs()
	{
		if (repository.refs == null)
		{
			return Collections.emptyList();
		}
		return repository.refs.get(sha);
	}

	/**
	 * Are there any refs associated with this commit?
	 * 
	 * @return
	 */
	public boolean hasRefs()
	{
		return !CollectionsUtil.isEmpty(getRefs());
	}

	/**
	 * Number of refs associated with the commit.
	 * 
	 * @return
	 */
	public int refCount()
	{
		if (!hasRefs())
		{
			return 0;
		}
		return getRefs().size();
	}

	public String getAuthorEmail()
	{
		return authorEmail;
	}

	void setAuthorEmail(String authorEmail)
	{
		this.authorEmail = authorEmail;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof GitCommit)
		{
			GitCommit other = (GitCommit) obj;
			return other.sha.equals(sha) && other.repository.equals(repository);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return 31 * sha.hashCode() + repository.hashCode();
	}

	/**
	 * Returns the collection of relative paths for files/folders affected by the commit.
	 * 
	 * @return
	 */
	private Collection<IPath> affectedPaths()
	{
		// TODO memoize?
		// git show --pretty="format:" --name-only bd61ad98
		// git diff-tree --name-only -r <commit-ish>
		IPath wd = repository.workingDirectory();
		IStatus status = repository.execute(ReadWrite.READ, wd, null, "diff-tree", "--name-only", "-r", sha); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (!status.isOK())
		{
			return Collections.emptyList();
		}
		String output = status.getMessage();
		List<String> lines = Arrays.asList(StringUtil.LINE_SPLITTER.split(output));
		lines = lines.subList(1, lines.size());
		return CollectionsUtil.map(lines, new IMap<String, IPath>()
		{
			public IPath map(String line)
			{
				return Path.fromPortableString(line);
			}
		});
	}

	/**
	 * Schedules a job to refresh the affected files for this commit.
	 */
	@SuppressWarnings("restriction")
	public void refreshAffectedFiles()
	{
		Workspace w = (Workspace) ResourcesPlugin.getWorkspace();
		RefreshManager rm = w.getRefreshManager();
		List<IResource> affectedResources = affectedResources();
		for (IResource r : affectedResources)
		{
			rm.refresh(r);
		}
	}
}
