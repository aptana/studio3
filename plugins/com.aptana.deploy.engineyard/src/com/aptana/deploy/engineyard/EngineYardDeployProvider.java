/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.engineyard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.deploy.IDeployProvider;
import com.aptana.terminal.views.TerminalView;

public class EngineYardDeployProvider implements IDeployProvider
{

	public static final String ID = "com.aptana.deploy.engineyard.provider"; //$NON-NLS-1$;

	public void deploy(IContainer selectedContainer, IProgressMonitor monitor)
	{
		IProject project = selectedContainer.getProject();
		TerminalView terminal = TerminalView.openView(project.getName(), project.getName(), project.getLocation());
		terminal.sendInput("ey deploy\n"); //$NON-NLS-1$
	}

	public boolean handles(IContainer selectedContainer)
	{
		if (selectedContainer == null || selectedContainer.getProject() == null)
		{
			return false;
		}

		// Engine Yard gem does not work in Windows
		if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			return false;
		}

		// If there's a config/ey.yml file, then they probably use engine yard
		IFile file = selectedContainer.getProject().getFile(Path.fromPortableString("config/ey.yml")); //$NON-NLS-1$
		return file.exists();
	}

	public String getDeployMenuName()
	{
		return null;
	}
}
