package com.aptana.deploy.redhat;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.deploy.redhat.messages"; //$NON-NLS-1$

	public static String DeployWizard_AutomaticGitCommitMessage;

	public static String RedHatAPI_LoginEmptyError;

	public static String RedHatAPI_PasswordEmptyError;

	public static String RedHatAPI_UnableToFindBinScriptsError;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
