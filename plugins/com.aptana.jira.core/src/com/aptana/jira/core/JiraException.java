/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

public class JiraException extends Exception
{

	private static final long serialVersionUID = 7533530410878760897L;

	public JiraException(String message)
	{
		super(message);
	}

	public JiraException(Throwable exception)
	{
		super(exception);
	}

	public JiraException(String message, Throwable exception)
	{
		super(message, exception);
	}
}
