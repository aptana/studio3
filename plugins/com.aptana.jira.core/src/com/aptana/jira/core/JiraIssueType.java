/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

public enum JiraIssueType
{

	BUG(Messages.JiraIssueType_Bug, "bug"), //$NON-NLS-1$
	FEATURE(Messages.JiraIssueType_Feature, "story"), //$NON-NLS-1$
	IMPROVEMENT(Messages.JiraIssueType_Improvement, "improvement"); //$NON-NLS-1$

	private String displayName;
	private String parameterName;

	public String getDisplayName()
	{
		return displayName;
	}

	public String getParameterName()
	{
		return parameterName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}

	private JiraIssueType(String displayName, String parameterName)
	{
		this.displayName = displayName;
		this.parameterName = parameterName;
	}
}
