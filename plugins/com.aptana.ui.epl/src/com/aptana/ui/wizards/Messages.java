/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
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

	public static String WizardFolderImportPage_ExistingFolderAsNewProject;

	public static String WizardFolderImportPage_SelectFolder;

	public static String WizardFolderImportPage_ProjectName;

	public static String WizardFolderImportPage_ErrorInitializingFolderImportWizard;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
