/**
 * Appcelerator Studio
 * Copyright (c) 2015 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */
package com.aptana.jira.core;

import com.aptana.core.util.StringUtil;

/**
 * @author Kondal Kolipaka
 */
public class JiraIssueConfig
{
	public JiraIssueType type = JiraIssueType.BUG;
	public JiraIssueSeverity severity = JiraIssueSeverity.MINOR;
	public String summary = StringUtil.EMPTY;
	public String description = StringUtil.EMPTY;
	public String label = StringUtil.EMPTY;
}
