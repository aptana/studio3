/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.ui.io.navigator.actions.messages"; //$NON-NLS-1$

    public static String EditorUtils_ERR_OpeningEditor;
    public static String EditorUtils_ERR_SavingRemoteFile;
    public static String EditorUtils_MSG_OpeningRemoteFile;
    public static String EditorUtils_MSG_RemotelySaving;
    public static String EditorUtils_OpenFileJob_Title;

    public static String EditorUtils_OverwritePrompt_Message;

	public static String EditorUtils_OverwritePrompt_Title;

	public static String FileSystemCopyAction_TTP;
    public static String FileSystemCopyAction_TXT;

    public static String FileSystemDeleteAction_Confirm_MultipleFiles;
    public static String FileSystemDeleteAction_Confirm_SingleFile;
    public static String FileSystemDeleteAction_Confirm_Title;
    public static String FileSystemDeleteAction_ERR_Delete;
    public static String FileSystemDeleteAction_JobTitle;
    public static String FileSystemDeleteAction_SubTask;
    public static String FileSystemDeleteAction_Task;
    public static String FileSystemDeleteAction_Text;

    public static String FileSystemNewAction_Text;

    public static String FileSystemPasteAction_TTP;
    public static String FileSystemPasteAction_TXT;

    public static String FileSystemRefreshAction_Text;
    public static String FileSystemRefreshAction_ToolTip;

    public static String FileSystemRenameAction_ERR_Message;
    public static String FileSystemRenameAction_ERR_Title;
    public static String FileSystemRenameAction_InputMessage;
    public static String FileSystemRenameAction_InputTitle;
    public static String FileSystemRenameAction_Text;
    public static String FileSystemRenameAction_ToolTip;

    public static String NewFileAction_InputMessage;
    public static String NewFileAction_InputTitle;
    public static String NewFileAction_Text;
    public static String NewFileAction_ToolTip;

    public static String NewFolderAction_InputMessage;
    public static String NewFolderAction_InputTitle;
    public static String NewFolderAction_JobTitle;
    public static String NewFolderAction_Text;
    public static String NewFolderAction_ToolTip;
    public static String NewFolderAction_WarningMessage;
    public static String NewFolderAction_WarningTitle;

    public static String OpenActionProvider_LBL_OpenWith;

    public static String OpenFileAction_ERR_FailedOpenFile;
    public static String OpenFileAction_Text;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
