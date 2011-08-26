/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.team.IMoveDeleteHook;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.team.core.history.IFileHistoryProvider;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
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
		// Ensure resource for newly created .git folder is loaded
		getProject().refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
		// look for .git sub dir
		final IResource dotGit = getProject().findMember(GitRepository.GIT_DIR);
		if (dotGit != null && dotGit.exists())
		{
			// if it exists and it actually is the right .git meta dir, let's mark it as team private
			URI gitDir = getGitRepositoryManager().gitDirForURL(getProject().getLocationURI());
			if (gitDir != null)
			{
				// Need to perform IPath based equals() check instead of URI based comparison
				// to deal with differences in the trailing / in two URIs
				IPath dotGitPath = dotGit.getLocation();
				if (dotGitPath != null && dotGitPath.equals(URIUtil.toPath((gitDir))))
				{
					dotGit.setTeamPrivateMember(true);
				}
			}
		}
	}

	private IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	@Override
	public String getID()
	{
		return ID;
	}

	public void deconfigure()
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
