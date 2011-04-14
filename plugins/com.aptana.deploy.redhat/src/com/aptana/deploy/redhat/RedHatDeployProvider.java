/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.redhat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.deploy.IDeployProvider;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.terminal.views.TerminalView;

public class RedHatDeployProvider implements IDeployProvider
{

	public static final String ID = "com.aptana.deploy.redhat.provider"; //$NON-NLS-1$

	public void deploy(IProject selectedProject, IProgressMonitor monitor)
	{

		try
		{
			IGitRepositoryManager manager = GitPlugin.getDefault().getGitRepositoryManager();
			GitRepository repo = manager.createOrAttach(selectedProject, new NullProgressMonitor());
			repo.index().stageFiles(repo.index().changedFiles());
			repo.index().commit(Messages.DeployWizard_AutomaticGitCommitMessage);

			TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
					selectedProject.getLocation());
			terminal.sendInput("git push\n"); //$NON-NLS-1$
		}
		catch (CoreException e)
		{
			RedHatPlugin.logError("Unable to deploy project", e); //$NON-NLS-1$
		}
	}

	/**
	 * TODO This is a hack of mine. I'm assuming this may change as their service changes.
	 */
	public boolean handles(IProject selectedProject)
	{
		// Check for a remote that looks like a RH one!
		GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(selectedProject);
		if (repo != null)
		{
			for (String remoteURL : repo.remoteURLs())
			{
				if (remoteURL.indexOf("rhcloud.com") != -1) //$NON-NLS-1$
				{
					return true;
				}
			}
		}
		return false;
	}

}
