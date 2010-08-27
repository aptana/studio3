/*******************************************************************************
 * Copyright (C) 2007, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.ui.internal.actions;

import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.team.core.RepositoryProvider;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.GitUIPlugin;

/**
 * Disconnects the Git team provider from a project.
 * <p>
 * Once disconnected, Git operations will no longer be available on the project.
 * </p>
 */
public class DisconnectProviderOperation implements IWorkspaceRunnable
{
	private final Collection<IAdaptable> projectList;

	/**
	 * Create a new disconnect operation.
	 * 
	 * @param projs
	 *            the collection of {@link IProject}s which should be disconnected from the Git team provider, and
	 *            returned to untracked/unmanaged status.
	 */
	public DisconnectProviderOperation(final Collection<IAdaptable> projs)
	{
		projectList = projs;
	}

	public void run(IProgressMonitor m) throws CoreException
	{
		if (m == null)
		{
			m = new NullProgressMonitor();
		}

		m.beginTask(Messages.DisconnectProviderOperation_DisconnectJob_Title, projectList.size() * 200);
		try
		{
			for (IAdaptable obj : projectList)
			{
				IResource res = (IResource) obj.getAdapter(IResource.class);
				IProject project;
				if (res instanceof IProject)
				{
					project = (IProject) res;
				}
				else
				{
					project = res.getProject();
				}

				if (project != null)
				{
					GitUIPlugin.trace("disconnecting project: " + project.getName()); //$NON-NLS-1$
					unmarkTeamPrivate(project);
					RepositoryProvider.unmap(project);
					getGitRepositoryManager().removeRepository(project);
					m.worked(100);

					project.refreshLocal(IResource.DEPTH_INFINITE, new SubProgressMonitor(m, 100));
				}
				else
				{
					m.worked(200);
				}
			}
		}
		finally
		{
			m.done();
		}
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	private void unmarkTeamPrivate(final IContainer p) throws CoreException
	{
		final IResource[] c;
		c = p.members(IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS);
		if (c != null)
		{
			for (int k = 0; k < c.length; k++)
			{
				if (c[k] instanceof IContainer)
				{
					unmarkTeamPrivate((IContainer) c[k]);
				}
				if (c[k].isTeamPrivateMember())
				{
					GitUIPlugin.trace("Setting to no longer be Team Private: " + c[k]); //$NON-NLS-1$
					c[k].setTeamPrivateMember(false);
				}
			}
		}
	}
}
