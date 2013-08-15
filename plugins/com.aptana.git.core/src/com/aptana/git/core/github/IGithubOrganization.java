/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.github;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * @author cwilliams
 */
public interface IGithubOrganization
{

	public int getID();

	/**
	 * Name of the org.
	 * 
	 * @return
	 */
	public String getName();

	public String getURL();

	public List<IGithubRepository> getRepos() throws CoreException;

}
