/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.commands;

public class SendFeedbackCommandHandler extends BrowserCommandHandler
{

	// the default url for sending feedback
	private static final String URL = "https://www.aptana.com/redirect/bugs"; //$NON-NLS-1$

	public SendFeedbackCommandHandler()
	{
		super(URL, "SendFeedback"); //$NON-NLS-1$
	}
}
