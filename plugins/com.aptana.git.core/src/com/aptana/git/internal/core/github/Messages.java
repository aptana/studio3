package com.aptana.git.internal.core.github;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.internal.core.github.messages"; //$NON-NLS-1$
	public static String GithubManager_CredentialSaveFailed;
	public static String GithubManager_LogoutFailed;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
