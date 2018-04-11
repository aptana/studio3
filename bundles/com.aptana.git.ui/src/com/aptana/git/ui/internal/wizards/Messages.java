/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.wizards.messages"; //$NON-NLS-1$

	public static String CloneJob_UnableToFindGitExecutableError;
	public static String CloneWizard_CloneFailedTitle;
	public static String CloneWizard_Job_title;

	public static String GithubForkWizard_FailedForkErr;

	public static String GithubForkWizard_ForkSubTaskName;

	public static String GithubForkWizard_UpstreamSubTaskName;

	public static String GithubRepositorySelectionPage_NoOwnerErr;

	public static String GithubRepositorySelectionPage_NoRepoNameErr;

	public static String GithubRepositorySelectionPage_OwnerLabel;

	public static String GithubRepositorySelectionPage_RepoNameLabel;

	public static String RepositorySelectionPage_CannotCreateDirectory_ErrorMessage;
	public static String RepositorySelectionPage_Description;
	public static String RepositorySelectionPage_Destination_Label;
	public static String RepositorySelectionPage_DestinatioNRequired_Message;
	public static String RepositorySelectionPage_DirectoryExists_ErrorMessage;
	public static String RepositorySelectionPage_LBL_Github;
	public static String RepositorySelectionPage_SourceURI_Label;
	public static String RepositorySelectionPage_SourceURIRequired_Message;
	public static String RepositorySelectionPage_Title;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
