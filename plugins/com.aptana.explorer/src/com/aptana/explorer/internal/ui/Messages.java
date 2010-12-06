/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.explorer.internal.ui.messages"; //$NON-NLS-1$

	public static String FilteringProjectView_LBL_FilteringFor;

	public static String FilteringProjectView_SearchByFilenameLabel;

	public static String FilteringProjectView_SearchContentLabel;

	public static String GitProjectView_BranchDirtyTooltipMessage;
	public static String GitProjectView_ChangedFilesFilterTooltip;
	public static String GitProjectView_createNewBranchOption;
	public static String GitProjectView_PullChangesTooltipMessage;
	public static String GitProjectView_PushChangesTooltipMessage;

	public static String SingleProjectView_OpenBrowserItem;
	public static String SingleProjectView_OpenSSHSubmenuLabel;
	public static String SingleProjectView_RecipesSubmenuLabel;
	public static String SingleProjectView_DeleteProjectMenuItem_LBL;
	public static String SingleProjectView_DeploymentSubmenuLabel;
	public static String SingleProjectView_SharingSubmenuLabel;
	public static String SingleProjectView_DatabaseSubmenuLabel;
	public static String SingleProjectView_DownloadItem;
	public static String SingleProjectView_MaintenanceSubmenuLabel;
	public static String SingleProjectView_RemoteSubmenuLabel;
	public static String SingleProjectView_ConfigVarsSubmenuLabel;
	public static String SingleProjectView_FTPSettingItem;
	public static String SingleProjectView_TTP_Commands;
	public static String SingleProjectView_TTP_Deploy;
	public static String SingleProjectView_UploadItem;
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
