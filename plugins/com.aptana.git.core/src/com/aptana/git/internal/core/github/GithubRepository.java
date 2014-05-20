/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.github;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubPullRequest;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRef;
import com.aptana.git.core.model.GitRepository;

/**
 * @author cwilliams
 */
public class GithubRepository implements IGithubRepository
{

	/**
	 * Keys used in JSON describing the repository.
	 */
	private static final String PARENT = "parent"; //$NON-NLS-1$
	private static final String SOURCE = "source"; //$NON-NLS-1$
	private static final String LOGIN = "login"; //$NON-NLS-1$
	private static final String OWNER = "owner"; //$NON-NLS-1$
	private static final String DEFAULT_BRANCH = "default_branch"; //$NON-NLS-1$
	private static final String FORK = "fork"; //$NON-NLS-1$
	private static final String PRIVATE = "private"; //$NON-NLS-1$
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String ATTR_SSH_URL = "ssh_url"; //$NON-NLS-1$

	private JSONObject json;

	public GithubRepository(JSONObject repo)
	{
		this.json = repo;
	}

	public long getID()
	{
		if (!json.containsKey(ID))
		{
			return -1L;
		}
		return (Long) json.get(ID);
	}

	public String getName()
	{
		return (String) json.get(NAME);
	}

	public String getFullName()
	{
		return getOwner() + '/' + getName();
	}

	public boolean isPrivate()
	{
		if (!json.containsKey(PRIVATE))
		{
			return false;
		}
		return (Boolean) json.get(PRIVATE);
	}

	public boolean isFork()
	{
		if (!json.containsKey(FORK))
		{
			return false;
		}
		return (Boolean) json.get(FORK);
	}

	public String getSSHURL()
	{
		return (String) json.get(ATTR_SSH_URL);
	}

	public String getDefaultBranch()
	{
		return (String) json.get(DEFAULT_BRANCH);
	}

	public String getOwner()
	{
		JSONObject owner = (JSONObject) json.get(OWNER);
		return (String) owner.get(LOGIN);
	}

	public IGithubRepository getParent() throws CoreException
	{
		if (!isFork())
		{
			return null;
		}

		if (!json.containsKey(PARENT))
		{
			getDetailedJSON();
		}
		// TODO Keep a cache of the repos inside the manager or something?
		return new GithubRepository((JSONObject) json.get(PARENT));
	}

	public IGithubRepository getSource() throws CoreException
	{
		if (!isFork())
		{
			return null;
		}

		if (!json.containsKey(SOURCE))
		{
			getDetailedJSON();
		}
		// TODO Keep a cache of the repos inside the manager or something?
		return new GithubRepository((JSONObject) json.get(SOURCE));
	}

	protected void getDetailedJSON() throws CoreException
	{
		this.json = (JSONObject) getAPI().get(getAPIURL());
	}

	/**
	 * The base URL for operations on this repo in Github's API
	 * 
	 * @return
	 */
	protected String getAPIURL()
	{
		return "repos/" + getOwner() + "/" + getName(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected GithubAPI getAPI()
	{
		return new GithubAPI(getGithubManager().getUser());
	}

	@SuppressWarnings("unchecked")
	public IGithubPullRequest createPullRequest(String title, String body, GitRepository head,
			IGithubRepository baseRepo, String baseBranch, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, Messages.GithubRepository_GeneratingPRTaskName, 100);
		String branch = head.currentBranch();

		// push current branch to origin first!
		sub.subTask(Messages.GithubRepository_PushingBranchSubtaskName);
		IStatus pushStatus = head.push(GitRepository.ORIGIN, branch);
		sub.worked(50);
		if (!pushStatus.isOK())
		{
			throw new CoreException(pushStatus);
		}

		sub.subTask(Messages.GithubRepository_SubmittingPRSubtaskName);
		JSONObject prObject = new JSONObject();
		prObject.put("title", title); //$NON-NLS-1$
		prObject.put("body", body); //$NON-NLS-1$
		prObject.put("head", getOwner() + ':' + branch); //$NON-NLS-1$
		prObject.put("base", baseBranch); //$NON-NLS-1$

		JSONObject result = (JSONObject) getAPI().post(
				((GithubRepository) baseRepo).getAPIURL() + "/pulls", prObject.toJSONString()); //$NON-NLS-1$
		return new GithubPullRequest(result);
	}

	protected IGithubManager getGithubManager()
	{
		return GitPlugin.getDefault().getGithubManager();
	}

	@Override
	public String toString()
	{
		return json.toJSONString();
	}

	public List<IGithubPullRequest> getOpenPullRequests() throws CoreException
	{
		JSONArray result = (JSONArray) getAPI().get(getAPIURL() + "/pulls"); //$NON-NLS-1$
		List<IGithubPullRequest> prs = new ArrayList<IGithubPullRequest>(result.size());
		for (Object blah : result)
		{
			JSONObject pr = (JSONObject) blah;
			prs.add(new GithubPullRequest(pr));
		}
		return prs;
	}

	public Set<String> getBranches()
	{
		IStatus status = GitExecutable.instance().runInBackground(null, "ls-remote", "--heads", getSSHURL()); //$NON-NLS-1$ //$NON-NLS-2$
		if (!status.isOK())
		{
			return Collections.emptySet();
		}
		String[] lines = StringUtil.LINE_SPLITTER.split(status.getMessage());
		Set<String> branches = new HashSet<String>(lines.length);
		for (String line : lines)
		{
			String pastSha = line.substring(40).trim();
			if (pastSha.startsWith(GitRef.REFS_HEADS))
			{
				branches.add(pastSha.substring(GitRef.REFS_HEADS.length()));
			}
		}
		return branches;
	}

	public List<IGithubRepository> getForks() throws CoreException
	{
		JSONArray result = (JSONArray) getAPI().get(getAPIURL() + "/forks"); //$NON-NLS-1$
		List<IGithubRepository> repos = new ArrayList<IGithubRepository>(result.size());
		for (Object blah : result)
		{
			JSONObject pr = (JSONObject) blah;
			repos.add(new GithubRepository(pr));
		}
		return repos;
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
		if (!(obj instanceof GithubRepository))
		{
			return false;
		}
		GithubRepository other = (GithubRepository) obj;
		if (getID() != other.getID())
		{
			return false;
		}
		return true;
	}

}
