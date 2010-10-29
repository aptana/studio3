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
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osgi.util.NLS;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.IGitRepositoryManager;

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
	protected ConnectProviderOperation(final IProject[] projects)
	{
		this.projects = projects;
	}

	public void run(IProgressMonitor m) throws CoreException
	{
		if (m == null)
		{
			m = new NullProgressMonitor();
		}

		m.beginTask(Messages.ConnectProviderOperation_ConnectingProviderJob_Title, 100 * projects.length);
		try
		{

			for (IProject project : projects)
			{
				m.setTaskName(NLS.bind(Messages.ConnectProviderOperation_ConnectingProjectJob_Title, project.getName()));
				getGitRepositoryManager().attachExisting(project, new SubProgressMonitor(m, 100));
			}
		}
		finally
		{
			m.done();
		}
	}

	private IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

}
