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

	public URL getURL() throws MalformedURLException;

	public int getNumber();

	public String getTitle();

	public String getBody();

}
