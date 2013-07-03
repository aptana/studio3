/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.github;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.json.simple.JSONObject;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.model.GitRepository;

/**
 * @author cwilliams
 */
public class GithubRepository implements IGithubRepository
{

	// @formatter:off
//  {
//    "id": 1296269,
//    "owner": {
//      "login": "octocat",
//      "id": 1,
//      "avatar_url": "https://github.com/images/error/octocat_happy.gif",
//      "gravatar_id": "somehexcode",
//      "url": "https://api.github.com/users/octocat"
//    },
//    "name": "Hello-World",
//    "full_name": "octocat/Hello-World",
//    "description": "This your first repo!",
//    "private": false,
//    "fork": false,
//    "url": "https://api.github.com/repos/octocat/Hello-World",
//    "html_url": "https://github.com/octocat/Hello-World",
//    "clone_url": "https://github.com/octocat/Hello-World.git",
//    "git_url": "git://github.com/octocat/Hello-World.git",
//    "ssh_url": "git@github.com:octocat/Hello-World.git",
//    "svn_url": "https://svn.github.com/octocat/Hello-World",
//    "mirror_url": "git://git.example.com/octocat/Hello-World",
//    "homepage": "https://github.com",
//    "language": null,
//    "forks": 9,
//    "forks_count": 9,
//    "watchers": 80,
//    "watchers_count": 80,
//    "size": 108,
//    "master_branch": "master",
//    "open_issues": 0,
//    "pushed_at": "2011-01-26T19:06:43Z",
//    "created_at": "2011-01-26T19:01:12Z",
//    "updated_at": "2011-01-26T19:14:43Z"
//  }
//  @formatter:on
	private static final String ATTR_SSH_URL = "ssh_url"; //$NON-NLS-1$
	private JSONObject json;
	private boolean hasDetailed;

	public GithubRepository(JSONObject repo)
	{
		this.json = repo;
		this.hasDetailed = false; // TODO Do a spot check of JSOn keys and check for one we only get in detailed
									// response?
	}

	public int getID()
	{
		return (Integer) json.get("id");
	}

	public String getName()
	{
		return (String) json.get("name");
	}

	public boolean isPrivate()
	{
		return (Boolean) json.get("private");
	}

	public boolean isFork()
	{
		return (Boolean) json.get("fork");
	}

	public String getSSHURL()
	{
		return (String) json.get(ATTR_SSH_URL);
	}

	public String getDefaultBranch()
	{
		return (String) json.get("default_branch");
	}

	public String getOwner()
	{
		JSONObject owner = (JSONObject) json.get("owner");
		return (String) owner.get("login");
	}

	public IGithubRepository getParent() throws CoreException
	{
		if (!isFork())
		{
			return null;
		}

		if (!json.containsKey("parent"))
		{
			getDetailedJSON();
		}
		// TODO Keep a cache of the repos inside the manager or something?
		return new GithubRepository((JSONObject) json.get("parent"));
	}

	protected void getDetailedJSON() throws CoreException
	{
		// Replace json with this new object and set a flag indicating we have the more detailed data now!
		this.json = (JSONObject) getAPI().get(getAPIURL());
		this.hasDetailed = true;
	}

	/**
	 * The base URL for operations on this repo in Github's API
	 * 
	 * @return
	 */
	protected String getAPIURL()
	{
		return "repos/" + getOwner() + "/" + getName();
	}

	protected GithubAPI getAPI()
	{
		return new GithubAPI(getGithubManager().getUser());
	}

	public IStatus createPullRequest(String title, String body, GitRepository repo)
	{
		String branch = repo.currentBranch();

		// push current branch to origin first!
		IStatus pushStatus = repo.push("origin", branch);
		if (!pushStatus.isOK())
		{
			return pushStatus;
		}

		try
		{
			IGithubRepository parent = getParent();
			JSONObject blah = new JSONObject();
			blah.put("title", title);
			blah.put("body", body);
			// TODO Allow user to choose branch on the fork to use as contents for PR?
			blah.put("head", getGithubManager().getUser().getUsername() + ":" + branch);
			// FIXME Allow user to choose the branch from parent to merge against. Default to the parent's default
			// branch
			blah.put("base", parent.getDefaultBranch());

			// TODO Do something with the response?
			getAPI().post(((GithubRepository) parent).getAPIURL() + "/pulls", blah.toJSONString());
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	protected IGithubManager getGithubManager()
	{
		return GitPlugin.getDefault().getGithubManager();
	}
}
