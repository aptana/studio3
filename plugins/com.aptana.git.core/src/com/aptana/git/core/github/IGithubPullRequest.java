/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.github;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author cwilliams
 */
public interface IGithubPullRequest
{

	/**
	 * The URL used to address the PR via the github API?
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public URL getURL() throws MalformedURLException;

	/**
	 * THE URL used to view the PR in a browser.
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public URL getHTMLURL() throws MalformedURLException;

	/**
	 * The PR number
	 * 
	 * @return
	 */
	public Long getNumber();

	/**
	 * Title of the PR
	 * 
	 * @return
	 */
	public String getTitle();

	/**
	 * Description of the PR.
	 * 
	 * @return
	 */
	public String getBody();

}
