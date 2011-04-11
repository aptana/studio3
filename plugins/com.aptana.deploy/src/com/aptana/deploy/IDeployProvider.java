package com.aptana.deploy;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IDeployProvider
{

	public void deploy(IProject project, IProgressMonitor monitor);

	public boolean handles(IProject selectedProject);
}
