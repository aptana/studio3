/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.build.ui.internal.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.build.ui.internal.preferences.messages"; //$NON-NLS-1$

	public static String ValidationPreferencePage_ERR_EmptyExpression;
	public static String ValidationPreferencePage_Filter_Description;
	public static String ValidationPreferencePage_Filter_SelectParticipant;
	public static String ValidationPreferencePage_Ignore_Message;
	public static String ValidationPreferencePage_Ignore_Title;
	public static String ValidationPreferencePage_LBL_Filter;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
