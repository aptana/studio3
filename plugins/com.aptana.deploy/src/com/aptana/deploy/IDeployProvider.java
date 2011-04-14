/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IDeployProvider
{

	/**
	 * Attempt to deploy the provided project.
	 * 
	 * @param project
	 * @param monitor
	 */
	public void deploy(IProject project, IProgressMonitor monitor);

	/**
	 * Is this a project that can be handled by this provider? This method is used to implicitly bind a project to a
	 * provider, when we haven't explicitly deployed via a provider yet. In real terms, this means looking to see if
	 * this project was set up to deploy to this provider outside the deploy wizard (and maybe outside the IDE).
	 * 
	 * @param selectedProject
	 * @return
	 */
	public boolean handles(IProject selectedProject);
}
