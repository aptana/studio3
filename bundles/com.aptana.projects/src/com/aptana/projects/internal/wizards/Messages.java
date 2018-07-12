/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.projects.internal.wizards.messages"; //$NON-NLS-1$

	public static String CommonWizardNewProjectCreationPage_location_has_existing_content_warning;

	public static String NewProjectWizard_CreateOp_Title;
	public static String NewProjectWizard_CreationProblem;
	public static String NewProjectWizard_ERR_FailToDisconnect;
	public static String NewProjectWizard_ERR_OpeningIndex;
	public static String NewProjectWizard_ERR_UnzipFile;
	public static String NewProjectWizard_filesOverwriteMessage;
	public static String NewProjectWizard_InternalError;
	public static String NewProjectWizard_ProjectPage_Description;
	public static String NewProjectWizard_ProjectPage_Title;
	public static String NewProjectWizard_Title;
	public static String NewProjectWizard_Step_Lbl;
	public static String NewProjectWizard_Warning_DirectoryExists;
	public static String NewProjectWizard_ZipFailure;
	public static String AbstractNewProjectWizard_CloningFromGitMsg;

	public static String AbstractNewProjectWizard_ProjectListener_NoDescriptor_Error;

	public static String AbstractNewProjectWizard_ProjectListener_TaskName;

	public static String AbstractNewProjectWizard_ProjectListenerErrorTitle;
	public static String AbstractNewProjectWizard_ProjectListenerSessionExpired;

	public static String OverwriteFilesSelectionDialog_overwriteFilesTitle;

	public static String ProjectTemplateSelectionPage_AvailableTemplates_TXT;
	public static String ProjectTemplateSelectionPage_Description;
	public static String ProjectTemplateSelectionPage_Title;
	public static String ProjectTemplateSelectionPage_UseTemplate_TXT;

	public static String PromoteToProjectWizard_PageDescription;
	public static String PromoteToProjectWizard_PageTitle;
	public static String PromoteToProjectWizard_WindowTitle;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
