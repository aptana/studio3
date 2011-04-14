/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.redhat.ui.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.deploy.redhat.ui.wizard.messages"; //$NON-NLS-1$

	public static String RedHatDeployWizardPage_ApplicationNameLabel;
	public static String RedHatDeployWizardPage_EmotyApplicationNameError;
	public static String RedHatDeployWizardPage_NoGitRepoNote;
	public static String RedHatDeployWizardPage_PublishApplicationLabel;
	public static String RedHatDeployWizardPage_Title;

	public static String RedHatLoginWizardPage_EmptyPasswordError;
	public static String RedHatLoginWizardPage_EmptyUserIDError;
	public static String RedHatLoginWizardPage_EnterCredentialsLabel;
	public static String RedHatLoginWizardPage_PasswordExample;
	public static String RedHatLoginWizardPage_PasswordLabel;
	public static String RedHatLoginWizardPage_SignupLink;
	public static String RedHatLoginWizardPage_SubmitButtonLabel;
	public static String RedHatLoginWizardPage_Title;
	public static String RedHatLoginWizardPage_UserIDExample;
	public static String RedHatLoginWizardPage_UserIDLabel;
	public static String RedHatLoginWizardPage_InvalidCredentialsError;

	public static String RedHatSignupWizardPage_Title;
	public static String RedHatSignupWizardPage_EnterCredentialsLabel;
	public static String RedHatSignupWizardPage_NamespaceLabel;
	public static String RedHatSignupWizardPage_NamespaceExample;
	public static String RedHatSignupWizardPage_UserIDLabel;
	public static String RedHatSignupWizardPage_UserIDExample;
	public static String RedHatSignupWizardPage_PasswordLabel;
	public static String RedHatSignupWizardPage_ConfirmPasswordLabel;
	public static String RedHatSignupWizardPage_InvalidNamespaceError;
	public static String RedHatSignupWizardPage_EmptyUserIDError;
	public static String RedHatSignupWizardPage_EmptyPasswordError;
	public static String RedHatSignupWizardPage_EmptyConfirmPasswordError;
	public static String RedHatSignupWizardPage_GemNotInstalledErrorMessage;
	public static String RedHatSignupWizardPage_GemNotInstalledMessage;
	public static String RedHatSignupWizardPage_GemNotInstalledTitle;
	public static String RedHatSignupWizardPage_PasswordsDontMatchError;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
