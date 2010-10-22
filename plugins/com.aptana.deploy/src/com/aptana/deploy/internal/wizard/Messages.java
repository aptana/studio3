/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.deploy.internal.wizard.messages"; //$NON-NLS-1$

	public static String FTPDeployComposite_AutoSync;
	public static String FTPDeployComposite_Download;
	public static String FTPDeployComposite_Synchronize;
	public static String FTPDeployComposite_Upload;
	public static String CapifyProjectPage_Description;
	public static String CapifyProjectPage_GenerateButtonLabel;
	public static String CapifyProjectPage_LinkText;
	public static String CapifyProjectPage_Title;
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
	public static String DeployWizardPage_AlreadyDeployedToHeroku;
	public static String DeployWizardPage_CapistranoLabel;
	public static String DeployWizardPage_FTPLabel;
	public static String DeployWizardPage_OtherDeploymentOptionsLabel;
	public static String DeployWizardPage_DeploymentOptionsLabel;
	public static String DeployWizardPage_ProvidersLabel;
	public static String DeployWizardPage_Title;
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

	public static String FTPDeployWizardPage_ProtocolLabel;
	public static String FTPDeployWizardPage_RemoteInfoLabel;
	public static String FTPDeployWizardPage_SiteNameLabel;
	public static String FTPDeployWizardPage_Title;
	public static String InstallCapistranoGemPage_Description;
	public static String InstallCapistranoGemPage_InstallGemLabel;
	public static String InstallCapistranoGemPage_Title;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
