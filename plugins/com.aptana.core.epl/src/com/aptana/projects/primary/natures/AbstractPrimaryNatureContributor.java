/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.primary.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * An abstract base implementation of an {@link IPrimaryNatureContributor}. This class provides an empty implementation
 * for the {@link #configure(IProject)} call.
 * 
 * @author sgibly@appcelerator.com
 */
public abstract class AbstractPrimaryNatureContributor implements IPrimaryNatureContributor
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.projects.primary.natures.IPrimaryNatureContributor#configure(org.eclipse.core.resources.IProject)
	 */
	public void configure(IProject project) throws CoreException
	{
		// No-Op
	}

	public IPath getLibraryContainerPath(IPath projectPath)
	{
		return projectPath;
	}
}
