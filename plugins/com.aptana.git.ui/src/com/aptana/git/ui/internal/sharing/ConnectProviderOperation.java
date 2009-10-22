/*******************************************************************************
 * Copyright (C) 2008, Shawn O. Pearce <spearce@spearce.org>
 * Copyright (C) 2008, Google Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.ui.internal.sharing;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.RepositoryProvider;

import com.aptana.git.model.GitRepository;
import com.aptana.git.ui.Activator;
import com.aptana.git.ui.internal.GitLightweightDecorator;

/**
 * Connects Eclipse to an existing Git repository
 */
public class ConnectProviderOperation implements IWorkspaceRunnable
{
	private final IProject[] projects;

	/**
	 * Create a new connection operation to execute within the workspace.
	 * 
	 * @param proj
	 *            the project to connect to the Git team provider.
	 */
	public ConnectProviderOperation(final IProject proj)
	{
		this(new IProject[] { proj });
	}

	/**
	 * Create a new connection operation to execute within the workspace.
	 * 
	 * @param projects
	 *            the projects to connect to the Git team provider.
	 */
	public ConnectProviderOperation(final IProject[] projects)
	{
		this.projects = projects;
	}

	public void run(IProgressMonitor m) throws CoreException
	{
		if (m == null)
		{
			m = new NullProgressMonitor();
		}

		m.beginTask("Connecting Git team provider", 100 * projects.length);
		try
		{

			for (IProject project : projects)
			{
				m.setTaskName(NLS.bind("Connecting project {0}", project.getName()));
				GitRepository repo = GitRepository.create(project.getLocationURI());
				m.worked(40);
				if (repo != null)
				{
					RepositoryProvider.map(project, RepositoryProvider.class.getName());
					m.worked(10);
					project.refreshLocal(IResource.DEPTH_INFINITE, new SubProgressMonitor(m, 50));
					GitLightweightDecorator.refresh();
				}
				else
				{
					Activator.logInfo("Attempted to share project without repository ignored :" //$NON-NLS-1$
							+ project);
					m.worked(60);
				}
			}
		}
		finally
		{
			m.done();
		}
	}
}
