/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.project;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.samples.ui.project.messages"; //$NON-NLS-1$

	public static String NewSampleProjectWizard_CreateOp_Title;
	public static String NewSampleProjectWizard_CreationProblems;
	public static String NewSampleProjectWizard_ERR_FailToDisconnect;
	public static String NewSampleProjectWizard_ERR_OpenIndexFile;
	public static String NewSampleProjectWizard_InternalError;
	public static String NewSampleProjectWizard_LocationExistsMessage;
	public static String NewSampleProjectWizard_ProjectPage_Description;
	public static String NewSampleProjectWizard_ProjectPage_Title;

	public static String NewSampleProjectWizard_ServicesError;
	public static String NewSampleProjectWizard_Title;
	public static String NewSampleProjectWizard_Warning_DirectoryExists;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
