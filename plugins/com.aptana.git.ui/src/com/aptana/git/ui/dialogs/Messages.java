package com.aptana.git.ui.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.dialogs.messages"; //$NON-NLS-1$

	public static String AddRemoteDialog_RemoteURILabel;
	public static String AddRemoteDialog_AddRemoteDialog_Title;
	public static String AddRemoteDialog_AddRemoteDialog_Message;
	public static String AddRemoteDialog_NonEmptyRemoteNameMessage;
	public static String AddRemoteDialog_NoWhitespaceRemoteNameMessage;
	
	public static String CreateBranchDialog_CreateBranchDialog_Message;
	public static String CreateBranchDialog_CreateBranchDialog_Title;
	public static String CreateBranchDialog_InvalidBranchNameMessage;
	public static String CreateBranchDialog_NonEmptyBranchNameMessage;
	public static String CreateBranchDialog_NoWhitespaceBranchNameMessage;
	public static String CreateBranchDialog_BranchAlreadyExistsMessage;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
