/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

import java.util.Map;

import com.aptana.core.util.CollectionsUtil;

@SuppressWarnings("nls")
public enum JiraIssueType
{

	// @formatter:off
	BUG(Messages.JiraIssueType_Bug, "Bug"),
	FEATURE(Messages.JiraIssueType_Feature, JiraManager.APTANA_STUDIO, "Story", JiraManager.TITANIUM_COMMUNITY, "New Feature"),
	IMPROVEMENT(Messages.JiraIssueType_Improvement, "Improvement");
	// @formatter:on

	private String displayName;

	/**
	 * Holds the parameter value for all projects. (if it never differs).
	 */
	private String parameterValue;

	/**
	 * If the parameter value differs by the project, this stores the mappings from project keys to values.
	 */
	private Map<String, String> projectKeysToParameterValues;

	public String getDisplayName()
	{
		return displayName;
	}

	String getParameterValue(String projectKey)
	{
		if (projectKeysToParameterValues != null)
		{
			String name = projectKeysToParameterValues.get(projectKey);
			if (name != null)
			{
				return name;
			}
		}
		return parameterValue;
	}

	@Override
	public String toString()
	{
		return displayName;
	}

	private JiraIssueType(String displayName, String parameterValue)
	{
		this.displayName = displayName;
		this.parameterValue = parameterValue;
	}

	private JiraIssueType(String displayName, String... projectKeysToParameterValues)
	{
		this.displayName = displayName;
		this.projectKeysToParameterValues = CollectionsUtil.newMap(projectKeysToParameterValues);
	}
}
