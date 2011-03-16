/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.ftp.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ui.ftp.internal.messages"; //$NON-NLS-1$

	public static String FTPAdvancedOptionsComposite_ConnectMode;
	public static String FTPAdvancedOptionsComposite_Detect;
	public static String FTPAdvancedOptionsComposite_Encoding;
	public static String FTPAdvancedOptionsComposite_InvalidPort;
	public static String FTPAdvancedOptionsComposite_ModeActive;
	public static String FTPAdvancedOptionsComposite_ModePassive;
	public static String FTPAdvancedOptionsComposite_Port;
	public static String FTPAdvancedOptionsComposite_Timezone;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
