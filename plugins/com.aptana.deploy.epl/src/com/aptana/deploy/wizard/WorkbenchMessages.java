package com.aptana.deploy.wizard;

import org.eclipse.osgi.util.NLS;

public class WorkbenchMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.deploy.wizard.messages"; //$NON-NLS-1$
	public static String DeployWizardPage_DeploymentOption;
	public static String DeployWizardPage_SelectDeploymentProvider;
	public static String DeployWizardPage_SelectYourDesiredDeploymentOption;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, WorkbenchMessages.class);
	}

	private WorkbenchMessages()
	{
	}
}
