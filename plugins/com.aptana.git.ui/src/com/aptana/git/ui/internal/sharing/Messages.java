package com.aptana.git.ui.internal.sharing;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.sharing.messages"; //$NON-NLS-1$
	public static String ConnectProviderOperation_ConnectingProjectJob_Title;
	public static String ConnectProviderOperation_ConnectingProviderJob_Title;
	public static String ExistingOrNewPage_CreateButton_Label;
	public static String ExistingOrNewPage_Description;
	public static String ExistingOrNewPage_ErrorFailedToRefreshRepository;
	public static String ExistingOrNewPage_PathColumn_Label;
	public static String ExistingOrNewPage_ProjectColumn_Label;
	public static String ExistingOrNewPage_RepositoryColumn_Label;
	public static String ExistingOrNewPage_Title;
	public static String ExistingOrNewPage_UnabletoFindGitExecutableError;
	public static String SharingWizard_failed;
	public static String SharingWizard_Title;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
