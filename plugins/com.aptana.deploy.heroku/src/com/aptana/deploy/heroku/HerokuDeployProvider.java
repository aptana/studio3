/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.heroku;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.deploy.IDeployProvider;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.terminal.views.TerminalView;

public class HerokuDeployProvider implements IDeployProvider
{

	public static final String ID = "com.aptana.deploy.heroku.provider"; //$NON-NLS-1$

	public void deploy(IProject selectedProject, IProgressMonitor monitor)
	{
		TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
				selectedProject.getLocation());
		terminal.sendInput("git push heroku master\n"); //$NON-NLS-1$
	}

	public boolean handles(IProject selectedProject)
	{
		// Check to see if a heroku remote exists on the git repo
		GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(selectedProject);
		if (repo != null)
		{
			for (String remote : repo.remotes())
			{
				if (remote.indexOf("heroku") != -1) //$NON-NLS-1$
				{
					return true;
				}
			}
			for (String remoteURL : repo.remoteURLs())
			{
				if (remoteURL.indexOf("heroku.com") != -1) //$NON-NLS-1$
				{
					return true;
				}
			}
		}
		return false;
	}

}
