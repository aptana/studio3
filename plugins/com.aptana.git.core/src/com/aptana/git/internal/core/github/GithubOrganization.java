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

	public int getID()
	{
		return (Integer) json.get(ID);
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
		List<JSONObject> result = (List<JSONObject>) new GithubAPI(GitPlugin.getDefault().getGithubManager().getUser())
				.get("orgs/" + getName() + "/repos"); //$NON-NLS-1$ //$NON-NLS-2$
		List<IGithubRepository> repoURLs = new ArrayList<IGithubRepository>(result.size());
		for (JSONObject repo : result)
		{
			repoURLs.add(new GithubRepository(repo));
		}
		return repoURLs;
	}

}
