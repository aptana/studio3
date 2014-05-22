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

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubOrganization;
import com.aptana.git.core.github.IGithubRepository;

public class GithubOrganization implements IGithubOrganization
{

	/**
	 * Keys used in JSON describing the org.
	 */
	private static final String LOGIN = "login"; //$NON-NLS-1$
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String URL = "url"; //$NON-NLS-1$

	private JSONObject json;

	public GithubOrganization(JSONObject json)
	{
		this.json = json;
	}

	public long getID()
	{
		if (!json.containsKey(ID))
		{
			return -1;
		}
		return (Long) json.get(ID);
	}

	public String getName()
	{
		return (String) json.get(LOGIN);
	}

	public String getURL()
	{
		return (String) json.get(URL);
	}

	public List<IGithubRepository> getRepos() throws CoreException
	{
		@SuppressWarnings("unchecked")
		List<JSONObject> result = (List<JSONObject>) getAPI().get("orgs/" + getName() + "/repos"); //$NON-NLS-1$ //$NON-NLS-2$
		List<IGithubRepository> repoURLs = new ArrayList<IGithubRepository>(result.size());
		for (JSONObject repo : result)
		{
			repoURLs.add(new GithubRepository(repo));
		}
		return repoURLs;
	}

	protected GithubAPI getAPI()
	{
		return new GithubAPI(GitPlugin.getDefault().getGithubManager().getUser());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (getID() ^ (getID() >>> 32));
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
		if (!(obj instanceof GithubOrganization))
		{
			return false;
		}
		GithubOrganization other = (GithubOrganization) obj;
		if (getID() != other.getID())
		{
			return false;
		}
		return true;
	}
}
