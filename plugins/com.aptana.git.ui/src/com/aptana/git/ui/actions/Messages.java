package com.aptana.git.ui.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$

	public static String GitProjectView_GitDiffDialogTitle;
	public static String GitProjectView_AttachGitRepo_jobTitle;
	
	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
