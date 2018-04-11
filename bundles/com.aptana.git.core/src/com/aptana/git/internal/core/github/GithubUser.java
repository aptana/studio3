/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.github;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.json.simple.JSONObject;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubOrganization;
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
		List<JSONObject> result = (List<JSONObject>) getAPI().get("user/repos"); //$NON-NLS-1$
		List<IGithubRepository> repoURLs = new ArrayList<IGithubRepository>(result.size());
		for (JSONObject repo : result)
		{
			repoURLs.add(createRepository(repo));
		}
		return repoURLs;
	}

	protected IGithubRepository createRepository(JSONObject repo)
	{
		return new GithubRepository(repo);
	}

	public IGithubRepository getRepo(String repoName) throws CoreException
	{
		return getGithubManager().getRepo(username, repoName);
	}

	protected IGithubManager getGithubManager()
	{
		return GitPlugin.getDefault().getGithubManager();
	}

	public Set<IGithubOrganization> getOrganizations() throws CoreException
	{
		@SuppressWarnings("unchecked")
		List<JSONObject> result = (List<JSONObject>) getAPI().get("user/orgs"); //$NON-NLS-1$
		Set<IGithubOrganization> repoURLs = new HashSet<IGithubOrganization>(result.size());
		for (JSONObject repo : result)
		{
			repoURLs.add(createOrganization(repo));
		}
		return repoURLs;
	}

	protected IGithubOrganization createOrganization(JSONObject repo)
	{
		return new GithubOrganization(repo);
	}

	protected GithubAPI getAPI()
	{
		return new GithubAPI(this);
	}

	public List<IGithubRepository> getAllRepos() throws CoreException
	{
		List<IGithubRepository> repos = getRepos();
		Set<IGithubOrganization> orgs = getOrganizations();
		if (!CollectionsUtil.isEmpty(orgs))
		{
			for (IGithubOrganization org : orgs)
			{
				repos.addAll(org.getRepos());
			}
		}
		return repos;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof IGithubUser))
		{
			return false;
		}
		IGithubUser other = (IGithubUser) obj;
		if (password == null)
		{
			if (other.getPassword() != null)
			{
				return false;
			}
		}
		else if (!password.equals(other.getPassword()))
		{
			return false;
		}
		if (username == null)
		{
			if (other.getUsername() != null)
			{
				return false;
			}
		}
		else if (!username.equals(other.getUsername()))
		{
			return false;
		}
		return true;
	}
}
