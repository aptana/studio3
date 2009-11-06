package com.aptana.git.core;

import java.net.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.team.IMoveDeleteHook;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.team.core.history.IFileHistoryProvider;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.internal.core.storage.GitFileHistoryProvider;

public class GitRepositoryProvider extends org.eclipse.team.core.RepositoryProvider
{

	public static final String ID = GitRepositoryProvider.class.getName();
	private GitFileHistoryProvider historyProvider;

	public GitRepositoryProvider()
	{
		// nothing
	}

	@Override
	public void configureProject() throws CoreException
	{
		// look for .git sub dir
		final IResource dotGit = getProject().findMember(GitRepository.GIT_DIR);
		if (dotGit != null && dotGit.exists())
		{
			// if it exists and it actually is the right .git meta dir, let's mark it as team private
			URI gitDir = GitRepository.gitDirForURL(getProject().getLocationURI());
			if (dotGit.getLocationURI().equals(gitDir))
			{
				dotGit.setTeamPrivateMember(true);
			}
		}
	}

	@Override
	public String getID()
	{
		return ID;
	}

	public void deconfigure() throws CoreException
	{
		// nothing
	}

	public synchronized IFileHistoryProvider getFileHistoryProvider()
	{
		if (historyProvider == null)
		{
			historyProvider = new GitFileHistoryProvider();
		}
		return historyProvider;
	}

	@Override
	public IMoveDeleteHook getMoveDeleteHook()
	{
		return new GitMoveDeleteHook();
	}
}
