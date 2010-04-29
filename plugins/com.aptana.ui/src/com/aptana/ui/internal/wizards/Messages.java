package com.aptana.ui.internal.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.internal.wizards.messages"; //$NON-NLS-1$

	public static String NewProjectWizard_CreateOp_Title;
	public static String NewProjectWizard_CreationProblem;
	public static String NewProjectWizard_ERR_CreatingIndex;
	public static String NewProjectWizard_ERR_OpeningIndex;
	public static String NewProjectWizard_InternalError;
	public static String NewProjectWizard_ProjectPage_Description;
	public static String NewProjectWizard_ProjectPage_Title;
	public static String NewProjectWizard_Title;
	public static String NewProjectWizard_Warning_DirectoryExists;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
