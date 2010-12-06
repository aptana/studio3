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
