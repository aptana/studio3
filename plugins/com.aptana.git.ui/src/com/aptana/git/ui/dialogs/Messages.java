package com.aptana.git.ui.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.dialogs.messages"; //$NON-NLS-1$

	public static String GitProjectView_CreateBranchDialog_Message;
	public static String GitProjectView_CreateBranchDialog_Title;

	public static String GitProjectView_InvalidBranchNameMessage;
	public static String GitProjectView_NonEmptyBranchNameMessage;
	public static String GitProjectView_NoWhitespaceBranchNameMessage;
	public static String GitProjectView_BranchAlreadyExistsMessage;
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
