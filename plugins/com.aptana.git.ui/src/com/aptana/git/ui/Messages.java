/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.messages"; //$NON-NLS-1$

	public static String DiffFormatter_NoContent;
	public static String GitUIPlugin_0;
	public static String GitUIPlugin_1;
	public static String GitUIPlugin_10;
	public static String GitUIPlugin_2;
	public static String GitUIPlugin_3;
	public static String GitUIPlugin_4;
	public static String GitUIPlugin_5;
	public static String GitUIPlugin_6;
	public static String GitUIPlugin_7;
	public static String GitUIPlugin_8;
	public static String GitUIPlugin_9;
	public static String GitUIPlugin_ToggleMessage;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
