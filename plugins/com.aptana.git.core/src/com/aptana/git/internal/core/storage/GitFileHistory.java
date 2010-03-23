package com.aptana.git.internal.core.storage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.provider.FileHistory;

import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;

public class GitFileHistory extends FileHistory
{

	private IResource resource;
	private final IFileRevision[] revisions;

	public GitFileHistory(IResource resource, int flags, IProgressMonitor monitor)
	{
		this.resource = resource;
		this.revisions = buildRevisions(flags, monitor);
	}

	private IFileRevision[] buildRevisions(int flags, IProgressMonitor monitor)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		try
		{
			if (resource == null || resource.getProject() == null)
				return new IFileRevision[0];
			GitRepository repo = GitRepository.getAttached(this.resource.getProject());
			if (repo == null)
				return new IFileRevision[0];
			// Need the repo relative path
			String resourcePath = repo.relativePath(resource);
			List<IFileRevision> revisions = new ArrayList<IFileRevision>();
			GitRevList list = new GitRevList(repo);
			int max = -1;
			if ((flags & IFileHistoryProvider.SINGLE_REVISION) == IFileHistoryProvider.SINGLE_REVISION)
			{
				max = 1;
			}
			list.walkRevisionListWithSpecifier(new GitRevSpecifier(resourcePath), max, subMonitor.newChild(95));
			List<GitCommit> commits = list.getCommits();
			for (GitCommit gitCommit : commits)
			{
				revisions.add(new CommitFileRevision(gitCommit, resource.getProjectRelativePath().toPortableString()));
			}
			return revisions.toArray(new IFileRevision[revisions.size()]);
		}
		finally
		{
			subMonitor.done();
		}
	}

	public IFileRevision[] getContributors(IFileRevision revision)
	{
		// TODO Return the versions right before the passed in version (previous commit(s))
		return null;
	}

	public IFileRevision getFileRevision(String id)
	{
		// TODO The id should be a commit sha, grab the CommitFileRevision for it...
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
		// TODO Return the version right after the passed in version (next commit(s))
		return null;
	}

}
