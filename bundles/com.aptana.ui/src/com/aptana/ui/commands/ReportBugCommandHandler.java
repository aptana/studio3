/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.commands;

public class ReportBugCommandHandler extends BrowserCommandHandler
{

	// the default url for reporting bug
	private static final String URL = "https://aptana.lighthouseapp.com/projects/35272-studio/tickets/new"; //$NON-NLS-1$

	public ReportBugCommandHandler()
	{
		super(URL, "ReportBug"); //$NON-NLS-1$
	}
}
