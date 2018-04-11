/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.formatter.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.formatter.ui.preferences.messages"; //$NON-NLS-1$

	public static String PropertyAndPreferencePage_configureProjectSettings;
	public static String PropertyAndPreferencePage_configureWorkspaceSettings;
	public static String PropertyAndPreferencePage_enableProjectSpecific;
	public static String AddRemoveList_add;
	public static String AddRemoveList_remove;
	public static String AddRemoveList_inputMessageTitle;
	public static String AddRemoveList_inputMessageText;
	public static String AddRemoveList_invalidBeginTagChar;
	public static String AddRemoveList_invalidCharInTag;
	// Positive number validator
	public static String PositiveNumberIsEmpty;
	public static String PositiveNumberIsInvalid;
	// Port validator
	public static String PortIsEmpty;
	public static String PortShouldBeInRange;
	public static String MinValueInvalid;
	// Text field validator
	public static String FieldIsEmpty;

	// Formatter OFF/ON
	public static String FormatterModifyDialog_OffOnTags;
	public static String FormatterOnOffPage_formatterOffOnInfo;
	public static String FormatterOnOffPage_formatterEnableOffOn;
	public static String FormatterOnOffPage_formatterOffTag;
	public static String FormatterOnOffPage_formatterOnTag;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
