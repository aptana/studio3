/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.buildpath.core;

import java.util.List;

import org.eclipse.core.resources.IProject;

/**
 * BuildPathContributor
 */
public interface IBuildPathContributor
{
	/**
	 * Contribute a list of bundle path entries to the project build path master list. This contributes to the list of
	 * globally "accepted valid" paths that can be assigned.
	 * 
	 * @return
	 */
	List<IBuildPathEntry> getBuildPathEntries();

	/**
	 * This will return the list of build paths we dynamically add for a given project. Examples include the project's
	 * Titanium Mobile SDK's JSCA (for the given version in the tiapp.xml), or Alloy JSCA.
	 * 
	 * @param project
	 * @return
	 */
	List<IBuildPathEntry> getBuildPathEntries(IProject project);
}
