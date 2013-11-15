/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.internal.core.github;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.internal.core.github.messages"; //$NON-NLS-1$

	public static String GithubManager_CredentialSaveFailed;
	public static String GithubManager_ERR_Github_NotLoggedIn;
	public static String GithubManager_LogoutFailed;

	public static String GithubRepository_GeneratingPRTaskName;
	public static String GithubRepository_PushingBranchSubtaskName;
	public static String GithubRepository_SubmittingPRSubtaskName;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
