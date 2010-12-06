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
				return new CommitFileRevision[0];
			GitRepository repo = getGitRepositoryManager().getAttached(this.resource.getProject());
			if (repo == null)
				return new CommitFileRevision[0];
			// Need the repo relative path
			IPath resourcePath = repo.relativePath(resource);
			List<IFileRevision> revisions = new ArrayList<IFileRevision>();
			GitRevList list = new GitRevList(repo);
			int max = -1;
			if ((flags & IFileHistoryProvider.SINGLE_REVISION) == IFileHistoryProvider.SINGLE_REVISION)
			{
				max = 1;
			}
			list.walkRevisionListWithSpecifier(new GitRevSpecifier(resourcePath.toOSString()), max, subMonitor.newChild(95));
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
			return new IFileRevision[0];
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
			return new IFileRevision[0];
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
