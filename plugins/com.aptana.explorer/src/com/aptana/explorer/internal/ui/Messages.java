/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.explorer.internal.ui.messages"; //$NON-NLS-1$

	public static String CommandsActionProvider_TTP_Commands;

	public static String FilteringProjectView_LBL_FilteringFor;
	public static String FilteringProjectView_SearchByFilenameLabel;
	public static String FilteringProjectView_SearchContentLabel;

	public static String GitProjectView_BranchDirtyTooltipMessage;
	public static String GitProjectView_ChangedFilesFilterTooltip;
	public static String GitProjectView_createNewBranchOption;
	public static String GitProjectView_PullChangesTooltipMessage;
	public static String GitProjectView_PushChangesTooltipMessage;
	public static String GitProjectView_SwitchBranchFailedTitle;

	public static String SingleProjectView_OpenProjectButton;
	public static String SingleProjectView_DeleteProjectMenuItem_LBL;
	public static String SingleProjectView_ClosedProjectSelectedLabel;
	public static String SingleProjectView_CreateProjectButtonLabel;
	public static String SingleProjectView_ImportProjectButtonLabel;
	public static String SingleProjectView_NoProjectsDescription;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
