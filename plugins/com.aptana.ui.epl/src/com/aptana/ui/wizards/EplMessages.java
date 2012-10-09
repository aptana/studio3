/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.wizards;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 * 
 * @author Robin
 */
public final class EplMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.wizards.eplmessages";//$NON-NLS-1$

	private EplMessages()
	{
		// Do not instantiate
	}

	public static String WizardFolderImportPage_ERR_FolderNotExist;
	public static String WizardFolderImportPage_ERR_NoFolderSelected;
	public static String WizardFolderImportPage_ERR_NoProjectName;
	public static String WizardFolderImportPage_ERR_ProjectNameExists;
	public static String WizardFolderImportPage_ExistingFolderAsNewProject;
	public static String WizardFolderImportPage_SelectFolder;
	public static String WizardFolderImportPage_ProjectName;
	public static String WizardFolderImportPage_ErrorInitializingFolderImportWizard;
	public static String WizardFolderImportPage_make_primary_label;
	public static String WizardFolderImportPage_set_primary_label;
	public static String WizardFolderImportPage_project_type_title;
	public static String WizardFolderImportPage_override_project_nature;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, EplMessages.class);
	}
}
