/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

public enum JiraIssuePriority
{

	CRITICAL(Messages.JiraIssuePriority_Critical), HIGH(Messages.JiraIssuePriority_High), MEDIUM(
			Messages.JiraIssuePriority_Medium), LOW(Messages.JiraIssuePriority_Low), TRIVIAL(
			Messages.JiraIssuePriority_Trivial), NONE(Messages.JiraIssuePriority_None);

	private String displayName;

	@Override
	public String toString()
	{
		return displayName;
	}

	private JiraIssuePriority(String displayName)
	{
		this.displayName = displayName;
	}
}
