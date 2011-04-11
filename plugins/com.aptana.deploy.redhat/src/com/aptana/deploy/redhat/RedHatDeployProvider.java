package com.aptana.deploy.redhat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.deploy.IDeployProvider;
import com.aptana.terminal.views.TerminalView;

public class RedHatDeployProvider implements IDeployProvider
{

	public void deploy(IProject selectedProject, IProgressMonitor monitor)
	{
		TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
				selectedProject.getLocation());
		terminal.sendInput("git push\n"); //$NON-NLS-1$
	}

	/**
	 * Red Hta can't handle existing projects. They need to be created from scratch.
	 */
	public boolean handles(IProject selectedProject)
	{
		// TODO Check for a remote that looks like a RH one!
		// GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(selectedProject);
		// if (repo != null)
		// {
		// for (String remote : repo.remotes())
		// {
		//				if (remote.indexOf("heroku") != -1) //$NON-NLS-1$
		// {
		// return true;
		// }
		// }
		// for (String remoteURL : repo.remoteURLs())
		// {
		//				if (remoteURL.indexOf("heroku.com") != -1) //$NON-NLS-1$
		// {
		// return true;
		// }
		// }
		// }
		return false;
	}

}
