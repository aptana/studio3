/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.github;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.json.simple.JSONObject;

import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.github.IGithubUser;

public class GithubUser implements IGithubUser
{

	private final String username;
	private final String password;

	public GithubUser(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public List<IGithubRepository> getRepos() throws CoreException
	{
		@SuppressWarnings("unchecked")
		List<JSONObject> result = (List<JSONObject>) new GithubAPI(this).get("user/repos"); //$NON-NLS-1$
		List<IGithubRepository> repoURLs = new ArrayList<IGithubRepository>(result.size());
		for (JSONObject repo : result)
		{
			repoURLs.add(new GithubRepository(repo));
		}
		return repoURLs;
	}

	public IGithubRepository getRepo(String repoName) throws CoreException
	{
		JSONObject result = (JSONObject) new GithubAPI(this).get("repos/" + username + '/' + repoName); //$NON-NLS-1$
		return new GithubRepository(result);
	}
}
