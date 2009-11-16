package com.aptana.git.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.core.messages"; //$NON-NLS-1$
	public static String GitMoveDeleteHook_CannotModifyRepository_ErrorMessage;
	public static String GitRepositoryProviderType_AttachingProject_Message;
	public static String GitRepositoryProviderType_AutoShareJob_Title;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
