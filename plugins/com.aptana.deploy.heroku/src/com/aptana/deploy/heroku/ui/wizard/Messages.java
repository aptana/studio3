/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.heroku.ui.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.deploy.heroku.ui.wizard.messages"; //$NON-NLS-1$

	public static String HerokuDeployWizardPage_ApplicationNameLabel;
	public static String HerokuDeployWizardPage_EmotyApplicationNameError;
	public static String HerokuDeployWizardPage_NoGitRepoNote;
	public static String HerokuDeployWizardPage_PublishApplicationLabel;
	public static String HerokuDeployWizardPage_Title;

	public static String HerokuLoginWizardPage_EmptyPasswordError;
	public static String HerokuLoginWizardPage_EmptyUserIDError;
	public static String HerokuLoginWizardPage_EnterCredentialsLabel;
	public static String HerokuLoginWizardPage_PasswordExample;
	public static String HerokuLoginWizardPage_PasswordLabel;
	public static String HerokuLoginWizardPage_SignupLink;
	public static String HerokuLoginWizardPage_SubmitButtonLabel;
	public static String HerokuLoginWizardPage_Title;
	public static String HerokuLoginWizardPage_UserIDExample;
	public static String HerokuLoginWizardPage_UserIDLabel;

	public static String HerokuSignupPage_InvalidEmail_Message;
	public static String HerokuSignupPage_SignupButtonLabel;
	public static String HerokuSignupPage_SignupNote;
	public static String HerokuSignupPage_Title;

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
