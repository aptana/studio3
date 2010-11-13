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
package com.aptana.editor.common.scripting.commands;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.editor.common.scripting.commands.messages"; //$NON-NLS-1$
	public static String CommandExecutionUtils_CouldNotCreateTemporaryFile;
	public static String CommandExecutionUtils_DefaultConsoleName;
	public static String CommandExecutionUtils_Input_File_Does_Not_Exist;
	public static String CommandExecutionUtils_ClickToFocusTypeEscapeToDismissWhenFocused;
	public static String CommandExecutionUtils_Unable_To_Write_To_Output_File;
	public static String FilterThroughCommandDialog_LBL_Command;
	public static String FilterThroughCommandDialog_LBL_ConsoleName;
	public static String FilterThroughCommandDialog_LBL_CreateNewDocument;
	public static String FilterThroughCommandDialog_LBL_Discard;
	public static String FilterThroughCommandDialog_LBL_Document;
	public static String FilterThroughCommandDialog_LBL_FromConsole;
	public static String FilterThroughCommandDialog_LBL_Input;
	public static String FilterThroughCommandDialog_LBL_InsertAsSnippet;
	public static String FilterThroughCommandDialog_LBL_InsertAsText;
	public static String FilterThroughCommandDialog_LBL_Line;
	public static String FilterThroughCommandDialog_LBL_None;
	public static String FilterThroughCommandDialog_LBL_Output;
	public static String FilterThroughCommandDialog_LBL_ReplaceDocument;
	public static String FilterThroughCommandDialog_LBL_ReplaceLine;
	public static String FilterThroughCommandDialog_LBL_ReplaceSelectedLines;
	public static String FilterThroughCommandDialog_LBL_ReplaceSelection;
	public static String FilterThroughCommandDialog_LBL_ReplaceWord;
	public static String FilterThroughCommandDialog_LBL_SelectedLines;
	public static String FilterThroughCommandDialog_LBL_Selection;
	public static String FilterThroughCommandDialog_LBL_ShowAsHTML;
	public static String FilterThroughCommandDialog_LBL_ShowAsToolTip;
	public static String FilterThroughCommandDialog_LBL_ShowEnvironment;
	public static String FilterThroughCommandDialog_LBL_ToConsole;
	public static String FilterThroughCommandDialog_LBL_Word;
	public static String FilterThroughCommandDialog_LBL_Clipboard;
	public static String FilterThroughCommandDialog_LBL_CopyToClipboard;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
