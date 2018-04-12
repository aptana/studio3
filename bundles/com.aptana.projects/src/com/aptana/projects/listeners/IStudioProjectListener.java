/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * Used to handle different events associated with Studio projects
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public interface IStudioProjectListener
{
	/**
	 * This method will be called after studio project has been created and could be used to add any custom setup,
	 * build, or cleanup for this project.
	 * 
	 * @param project
	 *            the newly created sample project
	 * @param monitor
	 *            optional monitor for the task
	 */
	public IStatus projectCreated(IProject project, IProgressMonitor monitor);

	/**
	 * This method will be called after a studio project is changed.
	 * 
	 * @param event
	 * @param monitor
	 * @return
	 */
	public IStatus projectChanged(IStudioProjectChangeEvent event, IProgressMonitor monitor);
}
