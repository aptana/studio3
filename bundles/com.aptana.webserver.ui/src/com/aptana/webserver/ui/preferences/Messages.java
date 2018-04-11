/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.webserver.ui.preferences.messages"; //$NON-NLS-1$

	public static String LocalWebServerPreferencePage_Address_Label;
	public static String LocalWebServerPreferencePage_Description;
	public static String LocalWebServerPreferencePage_Message;
	public static String LocalWebServerPreferencePage_Port_Label;
	public static String LocalWebServerPreferencePage_PortError_Message;
	public static String ServersPreferencePage_DeletePrompt_Message;
	public static String ServersPreferencePage_DeletePrompt_Title;
	public static String ServersPreferencePage_Title;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
