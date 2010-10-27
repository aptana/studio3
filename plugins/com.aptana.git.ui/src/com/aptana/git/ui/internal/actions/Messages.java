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
package com.aptana.git.ui.internal.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.actions.messages"; //$NON-NLS-1$

	public static String CommitAction_MultipleRepos_Message;
	public static String CommitAction_MultipleRepos_Title;
	public static String CommitAction_NoRepo_Message;
	public static String CommitAction_NoRepo_Title;
	public static String CommitDialog_Changes;
	public static String CommitDialog_NoFileSelected;
	public static String CommitDialog_StageAll;
	public static String CommitDialog_StageAllMarker;
	public static String CommitDialog_StagedChanges;
	public static String CommitDialog_UnstageAll;
	public static String CommitDialog_UnstageAllMarker;
	public static String CommitDialog_UnstagedChanges;
	public static String CommitDialog_UnstageSelected;
	public static String CommitDialog_UnstageSelectedMarker;
	public static String CommitDialog_CannotMerge_Error;
	public static String CommitDialog_CommitButton_Label;
	public static String CommitDialog_EnterMessage_Error;
	public static String CommitDialog_MessageLabel;
	public static String CommitDialog_PathColumnLabel;

	public static String CommitDialog_RevertLabel;
	public static String CommitDialog_StageFilesFirst_Error;
	public static String CommitDialog_StageSelected;
	public static String CommitDialog_StageSelectedMarker;
	public static String DisconnectProviderOperation_DisconnectJob_Title;
	public static String PullAction_RefreshJob_Title;
	public static String RevertAction_Label;
	public static String RevertAction_RefreshJob_Title;
	public static String SwitchBranchAction_BranchSwitch_Msg;
	public static String DeleteBranchAction_BranchDelete_Msg;
	public static String DeleteBranchAction_BranchDeletionFailed_Msg;
	public static String DeleteBranchAction_BranchDeletionFailed_Title;

	public static String DeleteBranchHandler_JobName;

	public static String GithubNetworkHandler_ViewName;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
