/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.github;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;

/**
 * @author cwilliams
 */
public interface IGithubUser
{

	// TODO We get back a lot of JSON info on the user. Maybe we want to store more info like email address,
	// public/private repo count, etc?

	public String getUsername();

	public String getPassword();

	/**
	 * Returns a List of IGithubRepository for the user.
	 * 
	 * @return
	 * @throws CoreException
	 */
	public List<IGithubRepository> getRepos() throws CoreException;

	/**
	 * Returns a specific repo by it's name (scoped under this user). TODO What about repos belonging to the user's
	 * org(s)?
	 * 
	 * @param repoName
	 * @return
	 * @throws CoreException
	 */
	public IGithubRepository getRepo(String repoName) throws CoreException;

	/**
	 * @return
	 * @throws CoreException
	 */
	public Set<IGithubOrganization> getOrganizations() throws CoreException;

	/**
	 * This will list the user's repos as well as any repos on any organizations the user is a member of.
	 * 
	 * @return
	 */
	public List<IGithubRepository> getAllRepos() throws CoreException;
}
