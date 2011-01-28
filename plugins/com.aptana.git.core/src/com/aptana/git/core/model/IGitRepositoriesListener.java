/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

/**
 * Listeners for changes to the collection of git repositories.
 * 
 * @author cwilliams
 */
public interface IGitRepositoriesListener
{

	/**
	 * A project has been mapped to a new repository (new to us, may have already existed on disk).
	 * 
	 * @param e
	 */
	public void repositoryAdded(RepositoryAddedEvent e);

	/**
	 * Called when a repository is unmapped from a project
	 * 
	 * @param e
	 */
	public void repositoryRemoved(RepositoryRemovedEvent e);
}
