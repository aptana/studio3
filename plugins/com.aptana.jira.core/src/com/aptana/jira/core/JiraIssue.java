/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

public class JiraIssue
{

	private final String name;
	private final String id;
	private final String url;

	public JiraIssue(String name, String id, String url)
	{
		this.name = name;
		this.id = id;
		this.url = url;
	}

	public String getName()
	{
		return name;
	}

	public String getId()
	{
		return id;
	}

	public String getUrl()
	{
		return url;
	}
}
