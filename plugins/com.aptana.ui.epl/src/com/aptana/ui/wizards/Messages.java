/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
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
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.wizards.messages";//$NON-NLS-1$

	private Messages()
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

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
