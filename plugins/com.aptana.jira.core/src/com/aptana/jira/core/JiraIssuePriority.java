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

	// @formatter:off
	CRITICAL(Messages.JiraIssuePriority_Critical, "Critical"), //$NON-NLS-1$
	HIGH(Messages.JiraIssuePriority_High, "High"), //$NON-NLS-1$
	MEDIUM(Messages.JiraIssuePriority_Medium, "Medium"), //$NON-NLS-1$
	LOW(Messages.JiraIssuePriority_Low, "Low"), //$NON-NLS-1$
	TRIVIAL(Messages.JiraIssuePriority_Trivial, "Trivial"), //$NON-NLS-1$
	NONE(Messages.JiraIssuePriority_None, "None"); //$NON-NLS-1$
	// @formatter:on

	private String displayName; // Translated name for UI
	private String parameterValue; // Untranslated value for JIRA

	@Override
	public String toString()
	{
		return displayName;
	}

	public String getParameterValue()
	{
		return parameterValue;
	}

	private JiraIssuePriority(String displayName, String parameterValue)
	{
		this.displayName = displayName;
		this.parameterValue = parameterValue;
	}
}
