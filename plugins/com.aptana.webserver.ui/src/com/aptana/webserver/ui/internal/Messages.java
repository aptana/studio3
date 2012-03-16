/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.webserver.ui.internal.messages"; //$NON-NLS-1$

	public static String SimpleWebServerPropertyDialog_BaseURL_Label;
	public static String SimpleWebServerPropertyDialog_DocRoot_Label;
	public static String SimpleWebServerPropertyDialog_DocumentRootError;
	public static String SimpleWebServerPropertyDialog_EmptyNameError;
	public static String SimpleWebServerPropertyDialog_InvalidURLError;
	public static String SimpleWebServerPropertyDialog_Name_Label;
	public static String SimpleWebServerPropertyDialog_ShellTitle;
	public static String SimpleWebServerPropertyDialog_Title;

	public static String ExternalWebServerPropertyDialog_StartCommandLabel;
	public static String ExternalWebServerPropertyDialog_StopCommandLabel;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
