/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ui.messages"; //$NON-NLS-1$

	public static String DialogUtils_HideMessage;

	public static String IDialogConstants_LBL_Apply;
	public static String IDialogConstants_LBL_Browse;

	public static String QuickMenuDialog_NoMatchesFound;

	public static String UIPlugin_ERR_FailToSetPref;
	public static String UIPlugin_ResetPerspective_Description;
	public static String UIPlugin_ResetPerspective_Title;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
