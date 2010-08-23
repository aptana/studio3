package com.aptana.projects.internal.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.projects.internal.wizards.messages"; //$NON-NLS-1$

	public static String NewProjectWizard_CreateOp_Title;
	public static String NewProjectWizard_CreationProblem;
	public static String NewProjectWizard_ERR_OpeningIndex;
	public static String NewProjectWizard_ERR_UnzipFile;
	public static String NewProjectWizard_InternalError;
	public static String NewProjectWizard_ProjectPage_Description;
	public static String NewProjectWizard_ProjectPage_Title;
	public static String NewProjectWizard_Title;
	public static String NewProjectWizard_Warning_DirectoryExists;

	public static String ProjectTemplateSelectionPage_AvailableTemplates_TXT;
	public static String ProjectTemplateSelectionPage_Description;
	public static String ProjectTemplateSelectionPage_Title;
	public static String ProjectTemplateSelectionPage_UseTemplate_TXT;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
