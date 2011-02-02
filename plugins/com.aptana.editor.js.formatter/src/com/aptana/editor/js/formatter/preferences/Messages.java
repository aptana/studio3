/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Shalom
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.js.formatter.preferences.Messages"; //$NON-NLS-1$
	public static String JSFormatterBlankLinesPage_afterFunctionDeclaration;
	public static String JSFormatterBlankLinesPage_afterFunctionDeclarationInExpression;
	public static String JSFormatterBlankLinesPage_blankLinesGroupLabel;
	public static String JSFormatterBlankLinesPage_existingBlankLinesGroupLabel;
	public static String JSFormatterBlankLinesPage_existingBlankLinesToPreserve;
	public static String JSFormatterBracesPage_blocks;
	public static String JSFormatterBracesPage_caseStateMent;
	public static String JSFormatterBracesPage_functionDeclaraion;
	public static String JSFormatterBracesPage_switchStatement;
	public static String JSFormatterCommentsPage_enableWrapping;
	public static String JSFormatterCommentsPage_formattingGroupLabel;
	public static String JSFormatterCommentsPage_maxLineWidth;
	public static String JSFormatterTabPage_indentGroupLabel;
	public static String JSFormatterTabPage_newLinesGroupLabel;
	public static String JSFormatterIndentationTabPage_indentationGeneralGroupLabel;
	public static String JSFormatterModifyDialog_blankLinesTabName;
	public static String JSFormatterModifyDialog_commentsTabName;
	public static String JSFormatterModifyDialog_jsFormatterTitle;
	public static String JSFormatterModifyDialog_intentationTabName;
	public static String JSFormatterModifyDialog_bracesTabName;

	public static String JSFormatterModifyDialog_newLinesTabName;
	public static String JSFormatterNewLinesPage_newLineBeforeBlocks;
	public static String JSFormatterNewLinesPage_newLineBeforeCatch;
	public static String JSFormatterNewLinesPage_newLineBeforeElse;
	public static String JSFormatterNewLinesPage_newLineBreakElseIf;
	public static String JSFormatterNewLinesPage_newLineBeforeFinally;
	public static String JSFormatterNewLinesPage_newLineBeforeIf;
	public static String JSFormatterNewLinesPage_newLineBeforeWhileInDo;

	public static String JSFormatterIndentationTabPage_statementsWithinBlocks;
	public static String JSFormatterIndentationTabPage_statementsWithinSwitch;
	public static String JSFormatterIndentationTabPage_statementsWithinCase;
	public static String JSFormatterIndentationTabPage_statementsWithinFunctions;
	public static String JSFormatterIndentationTabPage_statementsWithinJSGroups;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
