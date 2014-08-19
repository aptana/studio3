/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.github;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.json.simple.JSONObject;

import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubPullRequest;
import com.aptana.git.core.github.IGithubRepository;

/**
 * @author cwilliams
 */
class GithubPullRequest implements IGithubPullRequest
{

	/**
	 * Keys used in JSON describing the pull request.
	 */
	private static final String URL = "url"; //$NON-NLS-1$
	private static final String HTML_URL = "html_url"; //$NON-NLS-1$
	private static final String NUMBER = "number"; //$NON-NLS-1$
	private static final String TITLE = "title"; //$NON-NLS-1$
	private static final String BODY = "body"; //$NON-NLS-1$
	private static final String BASE2 = "base"; //$NON-NLS-1$
	private static final String REPO = "repo"; //$NON-NLS-1$
	private static final String HEAD = "head"; //$NON-NLS-1$
	private static final String REF = "ref"; //$NON-NLS-1$

	private JSONObject json;

	GithubPullRequest(JSONObject json)
	{
		this.json = json;
	}

	public URL getURL() throws MalformedURLException
	{
		return new URL(getRawURL());
	}

	public URL getHTMLURL() throws MalformedURLException
	{
		return new URL((String) json.get(HTML_URL));
	}

	private String getRawURL()
	{
		return (String) json.get(URL);
	}

	public Long getNumber()
	{
		return (Long) json.get(NUMBER);
	}

	public String getTitle()
	{
		return (String) json.get(TITLE);
	}

	public String getBody()
	{
		return (String) json.get(BODY);
	}

	public String getDisplayString()
	{
		return MessageFormat.format("#{0} - {1}", Long.toString(getNumber()), getTitle()); //$NON-NLS-1$
	}

	public IGithubRepository getHeadRepo()
	{
		JSONObject base = (JSONObject) json.get(HEAD);
		return new GithubRepository((JSONObject) base.get(REPO));
	}

	public IGithubRepository getBaseRepo()
	{
		JSONObject base = (JSONObject) json.get(BASE2);
		return new GithubRepository((JSONObject) base.get(REPO));
	}

	public IStatus merge(String commitMsg, IProgressMonitor monitor)
	{
		try
		{
			String input = null;
			if (!StringUtil.isEmpty(commitMsg))
			{
				JSONObject json = new JSONObject();
				json.put("commit_message", commitMsg);
				input = json.toJSONString();
			}

			IGithubRepository baseRepo = getBaseRepo();

			// TODO Grab the sha of the response so we can point to it?
			JSONObject result = (JSONObject) new GithubAPI(GitPlugin.getDefault().getGithubManager().getUser())
					.put(MessageFormat.format(
							"/repos/{0}/{1}/pulls/{2}/merge", baseRepo.getOwner(), baseRepo.getName(), getNumber()), input); //$NON-NLS-1$
			return Status.OK_STATUS;
		}
		catch (CoreException e)
		{
			return e.getStatus();
		}
	}

	public String getHeadRef()
	{
		JSONObject base = (JSONObject) json.get(HEAD);
		return (String) base.get(REF);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getRawURL() == null) ? 0 : getRawURL().hashCode());
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
		if (!(obj instanceof GithubPullRequest))
		{
			return false;
		}
		GithubPullRequest other = (GithubPullRequest) obj;
		if (getRawURL() == null)
		{
			if (other.getRawURL() != null)
			{
				return false;
			}
		}
		else if (!getRawURL().equals(other.getRawURL()))
		{
			return false;
		}
		return true;
	}
}
