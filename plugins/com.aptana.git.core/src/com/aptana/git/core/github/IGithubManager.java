/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.github;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

/**
 * @author cwilliams
 */
public interface IGithubManager
{

	/**
	 * Status code for reporting errors in operations due to user not being logged into Github API.
	 */
	public static final int GITHUB_LOGIN_CODE = 1234;

	/**
	 * Returns the currently logged in user.
	 * 
	 * @return
	 */
	public IGithubUser getUser();

	public IStatus login(String username, String password);

	public IStatus logout();

	/**
	 * Grabs the {@link IGithubRepository} model for a given repository by it's owner and name.
	 * 
	 * @param owner
	 * @param repoName
	 * @return
	 * @throws CoreException
	 *             if there is no logged in user, or if there is an error in grabbing the repo (permissions/404)
	 */
	public IGithubRepository getRepo(String owner, String repoName) throws CoreException;
}
