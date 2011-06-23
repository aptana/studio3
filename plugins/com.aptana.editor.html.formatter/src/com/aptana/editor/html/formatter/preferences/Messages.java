/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Shalom
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.html.formatter.preferences.Messages"; //$NON-NLS-1$
	public static String HTMLFormatterBlankLinesPage_afterElements;
	public static String HTMLFormatterBlankLinesPage_afterSpecialElements;
	public static String HTMLFormatterBlankLinesPage_beforeSpecialElements;
	public static String HTMLFormatterBlankLinesPage_blankLinesGroupLabel;
	public static String HTMLFormatterBlankLinesPage_existingBlankLinesGroupLabel;
	public static String HTMLFormatterBlankLinesPage_existingBlankLinesToPreserve;
	public static String HTMLFormatterCommentsPage_commentsInSeparateLines;
	public static String HTMLFormatterCommentsPage_enableWrapping;
	public static String HTMLFormatterCommentsPage_formattingGroupLabel;
	public static String HTMLFormatterCommentsPage_maxLineWidth;
	public static String HTMLFormatterIndentationTabPage_exclusionsMessage;
	public static String HTMLFormatterTabPage_exclusionsGroupLabel;
	public static String HTMLFormatterIndentationTabPage_indentationGeneralGroupLabel;
	public static String HTMLFormatterModifyDialog_blankLinesTabName;
	public static String HTMLFormatterModifyDialog_commentsTabName;
	public static String HTMLFormatterModifyDialog_htmlFormatterTitle;
	public static String HTMLFormatterModifyDialog_intentationTabName;
	public static String HTMLFormatterTabPage_newLinesGroupLabel;
	public static String HTMLFormatterTabPage_newLinesInEmptyTags;
	public static String HTMLFormatterModifyDialog_newLinesTabName;
	public static String HTMLFormatterNewLinesPage_exclusionsMessage;
	public static String HTMLFormatterModifyDialog_spacesTabName;
	public static String HTMLFormatterWhitespacesPage_spacesElementsGroupTitle;
	public static String HTMLFormatterWhitespacesPage_trimSpaces;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
