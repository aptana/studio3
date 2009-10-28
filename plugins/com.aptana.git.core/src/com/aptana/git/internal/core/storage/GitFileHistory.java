package com.aptana.git.internal.core.storage;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.history.IFileHistory;
import org.eclipse.team.core.history.IFileHistoryProvider;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.core.history.provider.FileHistory;

import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;

public class GitFileHistory extends FileHistory implements IFileHistory
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
		GitRepository repo = GitRepository.getAttached(this.resource.getProject());
		if (repo == null)
			return new IFileRevision[0];		
		// Need the repo relative path TODO Refactor this common code with the stuff in GitHistoryPage, lines 64-77
		String workingDirectory = repo.workingDirectory();
		String resourcePath = resource.getLocationURI().getPath();
		if (resourcePath.startsWith(workingDirectory))
		{
			resourcePath = resourcePath.substring(workingDirectory.length());
			if (resourcePath.startsWith("/") || resourcePath.startsWith("\\"))
				resourcePath = resourcePath.substring(1);
		}
		// What if we have some trailing slash or something?
		if (resourcePath.length() == 0)
		{
			resourcePath = repo.currentBranch();
		}		
		List<IFileRevision> revisions = new ArrayList<IFileRevision>();
		GitRevList list = new GitRevList(repo);
		int max = -1;
		if ((flags & IFileHistoryProvider.SINGLE_REVISION) == IFileHistoryProvider.SINGLE_REVISION)
		{
			max = 1;
		}
		list.walkRevisionListWithSpecifier(new GitRevSpecifier(resourcePath), max);
		List<GitCommit> commits = list.getCommits();
		for (GitCommit gitCommit : commits)
		{
			revisions.add(new CommitFileRevision(gitCommit, resource.getProjectRelativePath().toPortableString()));
		}
		return revisions.toArray(new IFileRevision[revisions.size()]);
	}

	@Override
	public IFileRevision[] getContributors(IFileRevision revision)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFileRevision getFileRevision(String id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFileRevision[] getFileRevisions()
	{
		final IFileRevision[] r = new IFileRevision[revisions.length];
		System.arraycopy(revisions, 0, r, 0, r.length);
		return r;
	}

	@Override
	public IFileRevision[] getTargets(IFileRevision revision)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
