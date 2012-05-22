/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.editor.findbar.impl.messages"; //$NON-NLS-1$

	public static String FindBarDecorator_Find_initial_text;

	public static String FindBarDecorator_LABEL_Scope_Current_File;

	public static String FindBarDecorator_LABEL_Scope_Enclosing_Project;

	public static String FindBarDecorator_LABEL_Scope_Open_Files;

	public static String FindBarDecorator_LABEL_Scope;

	public static String FindBarDecorator_LABEL_Scope_Shortcut;

	public static String FindBarDecorator_LABEL_Scope_Workspace;

	public static String FindBarDecorator_LABEL_ShowOptions;
	public static String FindBarDecorator_LABEL_ReplaceAll;
	public static String FindBarDecorator_LABEL_SearchBackward;
	public static String FindBarDecorator_LABEL_SearchSelection;
	public static String FindBarDecorator_LABEL_CaseSensitive;

	public static String FindBarDecorator_LABEL_Count_Match;
	public static String FindBarDecorator_LABEL_Elipses;
	public static String FindBarDecorator_LABEL_Find;

	public static String FindBarDecorator_LABEL_No_History;
	public static String FindBarDecorator_LABEL_RegularExpression;
	public static String FindBarDecorator_LABEL_WholeWord;
	public static String FindBarDecorator_LABEL_Replace;
	public static String FindBarDecorator_LABEL_ReplaceFind;
	public static String FindBarDecorator_MSG_StringNotFound;
	public static String FindBarDecorator_MSG_Wrapped;
	public static String FindBarDecorator_TOOLTIP_HideFindBar;

	public static String FindBarDecorator_TOOLTIP_History;
	public static String FindBarDecorator_TOOLTIP_Scope_menu_item;

	public static String FindBarDecorator_TOOLTIP_ShowMatchCount;
	public static String FindBarDecorator_TOOLTIP_MatchCount;
	public static String FindBarDecorator_MSG_ReadOnly;
	public static String FindBarDecorator_MSG_Replaced;
	public static String FindBarDecorator_MSG_ReplaceNeedsFind;
	public static String FindBarDecorator_MSG_ReplaceNeedsToMatchSelectedText;
	public static String FindBarDecorator_Replace_initial_text;

	public static String FindBarDecorator_ReplaceError;

	public static String FindBarActions_TOOLTIP_FocusReplaceCombo;
	public static String FindBarActions_TOOLTIP_FocusFindCombo;

	public static String FindHelper_Error_active_page_null;

	public static String FindHelper_Error_workbench_window_null;

	public static String FindInOpenDocuments_NoFileFound;
	public static String FindInOpenDocuments_FileNotInWorkspace;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
