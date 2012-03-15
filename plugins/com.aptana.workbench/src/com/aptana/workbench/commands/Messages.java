/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.workbench.commands;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.workbench.commands.messages"; //$NON-NLS-1$

	public static String EditBundleJob_BundleHasNoRepository_Error;
	public static String EditBundleJob_CantCreateUserBundlesDir_Error;
	public static String EditBundleJob_GitCloneFailed_Error;
	public static String EditBundleJob_Name;
	public static String EditBundleJob_RequiresGitError;
	public static String EditorCommandsMenuContributor_CommandsForOtherScopes;
	public static String EditorCommandsMenuContributor_ErrorExecutingCommandNullResult;
	public static String EditorCommandsMenuContributor_LBL_EditBundle;
	public static String EditorCommandsMenuContributor_MSG_CommandNotDefined;
	public static String EditorCommandsMenuContributor_TITLE_CommandNotDefined;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
