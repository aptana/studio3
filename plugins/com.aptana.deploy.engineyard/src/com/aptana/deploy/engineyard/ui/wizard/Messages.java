/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.engineyard.ui.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.deploy.engineyard.ui.wizard.messages"; //$NON-NLS-1$

	public static String EngineYardDeployWizardPage_ApplicationLinkLabel;
	public static String EngineYardDeployWizardPage_ApplicationNameLabel;
	public static String EngineYardDeployWizardPage_ApplicationNoteLabel;
	public static String EngineYardDeployWizardPage_Title;

	public static String EngineYardLoginWizardPage_EmailAddressExample;
	public static String EngineYardLoginWizardPage_EmailAddressLabel;
	public static String EngineYardLoginWizardPage_EmptyEmailAddressLabel;
	public static String EngineYardLoginWizardPage_EmptyPasswordLabel;
	public static String EngineYardLoginWizardPage_EnterCredentialsLabel;
	public static String EngineYardLoginWizardPage_ErrorTitle;
	public static String EngineYardLoginWizardPage_InvalidCredentails_Message;
	public static String EngineYardLoginWizardPage_PasswordExample;
	public static String EngineYardLoginWizardPage_PasswordLabel;
	public static String EngineYardLoginWizardPage_SignupLinkLabel;
	public static String EngineYardLoginWizardPage_SubmitButtonLabel;
	public static String EngineYardLoginWizardPage_Success_Message;
	public static String EngineYardLoginWizardPage_SuccessTitle;
	public static String EngineYardLoginWizardPage_Title;

	public static String EngineYardSignupPage_EmailAddressExample;
	public static String EngineYardSignupPage_EmailAddressLabel;
	public static String EngineYardSignupPage_EmptyEmailAddressLabel;
	public static String EngineYardSignupPage_EnterCredentialsLabel;
	public static String EngineYardSignupPage_InvalidEmail_Message;
	public static String EngineYardSignupPage_SignupButtonLabel;
	public static String EngineYardSignupPage_SignupNote;
	public static String EngineYardSignupPage_Title;

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
