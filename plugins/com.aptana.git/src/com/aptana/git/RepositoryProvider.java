package com.aptana.git;

import java.net.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.aptana.git.model.GitRepository;

public class RepositoryProvider extends org.eclipse.team.core.RepositoryProvider
{

	public static final String ID = RepositoryProvider.class.getName();

	public RepositoryProvider()
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

	@Override
	public void deconfigure() throws CoreException
	{
		// nothing
	}

}
