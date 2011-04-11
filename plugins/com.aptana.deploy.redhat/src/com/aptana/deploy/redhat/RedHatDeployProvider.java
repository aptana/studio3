package com.aptana.deploy.redhat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.deploy.IDeployProvider;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.terminal.views.TerminalView;

public class RedHatDeployProvider implements IDeployProvider
{

	public static final String ID = "com.aptana.deploy.redhat.provider"; //$NON-NLS-1$

	public void deploy(IProject selectedProject, IProgressMonitor monitor)
	{
		TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
				selectedProject.getLocation());
		terminal.sendInput("git push\n"); //$NON-NLS-1$
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
