/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.index.core.build.BuildContext;

/**
 * @author cwilliams
 */
public interface IBuildParticipant
{

	/**
	 * Clean is called on a whole project. This method is called on every build participant regardless of the content
	 * type bindings. Implementations are expected to introspect n the {@link IProject} to determine if they need to
	 * operate.
	 * 
	 * @param project
	 * @param monitor
	 */
	public void clean(IProject project, IProgressMonitor monitor);

	/**
	 * Called before we run any build, so we can do any sort of pre-init/batch work.
	 * 
	 * @param kind
	 * @param monitor
	 */
	public void buildStarting(IProject project, int kind, IProgressMonitor monitor);

	/**
	 * Called after we finish doing the per-file processing in the build.
	 * 
	 * @param monitor
	 */
	public void buildEnding(IProgressMonitor monitor);

	/**
	 * Grab the priority of the build participant. Used to order how they get called during build process.
	 * 
	 * @return
	 */
	public int getPriority();

	/**
	 * @param priority
	 */
	public void setPriority(int priority);

	/**
	 * Called on an individual file. For incremental builds we traverse the diff and call this for every updated/added
	 * file. For full builds we traverse the project to collect the files and call this once per file.
	 * 
	 * @param context
	 * @param monitor
	 */
	public void buildFile(BuildContext context, IProgressMonitor monitor);

	/**
	 * Called on an individual file. For incremental builds we traverse the diff and call this for every updated/added
	 * file.
	 * 
	 * @param context
	 * @param monitor
	 */
	public void deleteFile(BuildContext context, IProgressMonitor monitor);
}