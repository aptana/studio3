/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.handlers;

import org.eclipse.core.resources.IProject;

public interface ISampleProjectHandler
{

	/**
	 * This method will be called after a sample project has been created and could be used to add any custom setup,
	 * build, or cleanup for this project.
	 * 
	 * @param project
	 *            the newly created sample project
	 */
	public void projectCreated(IProject project, Object data);
}
