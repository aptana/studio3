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

import org.json.simple.JSONObject;

import com.aptana.git.core.github.IGithubPullRequest;

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

	private JSONObject json;

	GithubPullRequest(JSONObject json)
	{
		this.json = json;
	}

	public URL getURL() throws MalformedURLException
	{
		return new URL((String) json.get(URL));
	}

	public URL getHTMLURL() throws MalformedURLException
	{
		return new URL((String) json.get(HTML_URL));
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

}
