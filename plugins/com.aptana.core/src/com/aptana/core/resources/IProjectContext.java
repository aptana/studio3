package com.aptana.core.resources;

import org.eclipse.core.resources.IProject;

public interface IProjectContext
{

	public IProject getActiveProject();

	public void setActiveProject(IProject project);

}
