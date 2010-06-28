package com.aptana.ui.projects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.aptana.ui.UIPlugin;

public class WebProjectNature implements IProjectNature
{

	public static final String ID = UIPlugin.PLUGIN_ID + ".webnature"; //$NON-NLS-1$

	private IProject project;

	@Override
	public void configure() throws CoreException
	{
	}

	@Override
	public void deconfigure() throws CoreException
	{
	}

	@Override
	public IProject getProject()
	{
		return project;
	}

	@Override
	public void setProject(IProject project)
	{
		this.project = project;
	}
}
