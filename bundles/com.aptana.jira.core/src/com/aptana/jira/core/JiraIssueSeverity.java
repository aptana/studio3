/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

public enum JiraIssueSeverity
{

	// @formatter:off
	BLOCKER(Messages.JiraIssueSeverity_Blocker, "Blocker"), //$NON-NLS-1$
	MAJOR(Messages.JiraIssueSeverity_Major, "Major"), //$NON-NLS-1$
	MINOR(Messages.JiraIssueSeverity_Minor, "Minor"), //$NON-NLS-1$
	TRIVIAL(Messages.JiraIssueSeverity_Trivial, "Trivial"); //$NON-NLS-1$
	// @formatter:on

	private String displayName; // Translated name for UI
	private String parameterValue; // Untranslated value for JIRA

	@Override
	public String toString()
	{
		return displayName;
	}

	private JiraIssueSeverity(String displayName, String parameterValue)
	{
		this.displayName = displayName;
		this.parameterValue = parameterValue;
	}

	public String getParameterValue()
	{
		return "\"customfield_10090\": { \"value\": \"" + parameterValue + "\" }"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
