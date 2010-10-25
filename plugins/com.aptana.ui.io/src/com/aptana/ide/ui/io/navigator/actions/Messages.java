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
