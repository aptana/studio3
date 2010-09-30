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

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
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
