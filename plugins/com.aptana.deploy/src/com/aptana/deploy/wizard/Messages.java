package com.aptana.deploy.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.deploy.wizard.messages"; //$NON-NLS-1$
	
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
	public static String HerokuSignupPage_SignupButtonLabel;
	public static String HerokuSignupPage_SignupNote;
	public static String HerokuSignupPage_Title;
	public static String DeployWizard_AutomaticGitCommitMessage;
	public static String DeployWizard_FailureToGrabHerokuSignupJSError;
	public static String DeployWizardPage_CapistranoLabel;
	public static String DeployWizardPage_FTPLabel;
	public static String DeployWizardPage_OtherDeploymentOptionsLabel;
	public static String DeployWizardPage_DeploymentOptionsLabel;
	public static String DeployWizardPage_ProvidersLabel;
	public static String DeployWizardPage_Title;
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
