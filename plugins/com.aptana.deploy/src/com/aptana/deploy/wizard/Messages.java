package com.aptana.deploy.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.deploy.wizard.messages"; //$NON-NLS-1$
	
	public static String DeployWizard_AutomaticGitCommitMessage;
	public static String DeployWizard_FailureToGrabHerokuSignupJSError;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
