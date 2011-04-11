package com.aptana.deploy.capistrano;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.deploy.IDeployProvider;
import com.aptana.terminal.views.TerminalView;

public class CapistranoDeployProvider implements IDeployProvider
{

	public void deploy(IProject selectedProject, IProgressMonitor monitor)
	{
		TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
				selectedProject.getLocation());
		terminal.sendInput("cap deploy\n"); //$NON-NLS-1$
	}

	public boolean handles(IProject selectedProject)
	{
		return selectedProject.getFile("Capfile").exists(); //$NON-NLS-1$
	}

}
