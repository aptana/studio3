package com.aptana.deploy.engineyard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;
import com.aptana.terminal.views.TerminalView;

public class EngineYardDeployProvider implements IDeployProvider
{

	public void deploy(IProject selectedProject, IProgressMonitor monitor)
	{
		TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
				selectedProject.getLocation());
		terminal.sendInput("ey deploy\n"); //$NON-NLS-1$
	}

	public boolean handles(IProject selectedProject)
	{
		DeployType type = DeployPreferenceUtil.getDeployType(selectedProject);

		// Engine Yard gem does not work in Windows
		if (!Platform.getOS().equals(Platform.OS_WIN32))
		{
			if (type.equals(DeployType.ENGINEYARD))
			{
				return true;
			}
		}

		return false;
	}

}
