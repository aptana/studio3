/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.js.debug.ui.internal.preferences.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String JSDetailFormattersPreferencePage_DetailFormatters;
	public static String JSDetailFormattersPreferencePage_OverrideDefault;
	public static String JSDetailFormattersPreferencePage_ShowVariableDetails;
	public static String JSDetailFormattersPreferencePage_AsLabelForVariablesWithDetailFormatters;
	public static String JSDetailFormattersPreferencePage_AsLabelForAllVariables;
	public static String JSDetailFormattersPreferencePage_InDetailPaneOnly;
	public static String JSDetailFormattersPreferencePage_TypesWithDetailFormatters;
	public static String JSDetailFormattersPreferencePage_DetailFormatterCodeSnippetDefinedForSelectedType;
	public static String JSDetailFormattersPreferencePage_Add;
	public static String JSDetailFormattersPreferencePage_AllowToCreateNewDetailFormatter;
	public static String JSDetailFormattersPreferencePage_Edit;
	public static String JSDetailFormattersPreferencePage_EditSelectedDetailFormatter;
	public static String JSDetailFormattersPreferencePage_Remove;
	public static String JSDetailFormattersPreferencePage_RemoveAllSelectedDetailFormatters;

	public static String JSDebugPreferencePage_JavascriptDebugOptions;
	public static String JSDebugPreferencePage_SuspendOnUncaughtExceptions;
	public static String JSDebugPreferencePage_SuspendOnAllExceptions;
	public static String JSDebugPreferencePage_ConfirmExitWhenDebuggerActive;
}
