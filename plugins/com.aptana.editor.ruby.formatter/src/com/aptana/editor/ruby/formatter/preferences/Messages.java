/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.editor.ruby.formatter.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Alexey
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.ruby.formatter.preferences.messages"; //$NON-NLS-1$
	public static String RubyFormatterBlankLinesPage_afterRequire;
	public static String RubyFormatterBlankLinesPage_beforeFirstDeclare;
	public static String RubyFormatterBlankLinesPage_beforeMethodsDeclare;
	public static String RubyFormatterBlankLinesPage_beforeNestedClass;
	public static String RubyFormatterBlankLinesPage_beforeNestedModule;
	public static String RubyFormatterBlankLinesPage_betweenClasses;
	public static String RubyFormatterBlankLinesPage_betweenMethods;
	public static String RubyFormatterBlankLinesPage_betweenModules;
	public static String RubyFormatterBlankLinesPage_blankLines;
	public static String RubyFormatterBlankLinesPage_blanksWithinClassesAndModules;
	public static String RubyFormatterBlankLinesPage_emptyLinesToPreserve;
	public static String RubyFormatterBlankLinesPage_existingBlankLines;
	public static String RubyFormatterCommentsPage_commentFormatting;
	public static String RubyFormatterCommentsPage_enableCommentWrapping;
	public static String RubyFormatterCommentsPage_maxCommentWidth;
	public static String RubyFormatterModifyDialog_blanksTabTitle;
	public static String RubyFormatterModifyDialog_commentsTabTitle;
	public static String RubyFormatterModifyDialog_indentationTabTitle;
	public static String RubyFormatterModifyDialog_rubyFormatterTitle;
	public static String RubyFormatterPreferencePage_description;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
