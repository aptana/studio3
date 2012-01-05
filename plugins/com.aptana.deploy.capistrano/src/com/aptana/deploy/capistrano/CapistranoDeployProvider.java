/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.capistrano;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.deploy.IDeployProvider;
import com.aptana.terminal.views.TerminalView;

public class CapistranoDeployProvider implements IDeployProvider
{

	public static final String ID = "com.aptana.deploy.capistrano.provider"; //$NON-NLS-1$

	public void deploy(IContainer selectedContainer, IProgressMonitor monitor)
	{
		IProject project = selectedContainer.getProject();
		TerminalView terminal = TerminalView.openView(project.getName(), project.getName(), project.getLocation());
		terminal.sendInput("cap deploy\n"); //$NON-NLS-1$
	}

	public boolean handles(IContainer selectedContainer)
	{
		if (selectedContainer == null || selectedContainer.getProject() == null)
		{
			return false;
		}
		return selectedContainer.getProject().getFile("Capfile").exists(); //$NON-NLS-1$
	}

	public String getDeployMenuName()
	{
		return null;
	}
}
