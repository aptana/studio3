/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.browser;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.browser.messages"; //$NON-NLS-1$

	public static String WebBrowserViewer_CustomSize_Message;
	public static String WebBrowserViewer_CustomSize_Title;
	public static String WebBrowserViewer_ERR_InvalidHeight;
	public static String WebBrowserViewer_ERR_InvalidWidth;
	public static String WebBrowserViewer_LBL_Back;
	public static String WebBrowserViewer_LBL_Custom;
	public static String WebBrowserViewer_LBL_Forward;
	public static String WebBrowserViewer_LBL_FullEditor;
	public static String WebBrowserViewer_LBL_Go;
	public static String WebBrowserViewer_LBL_Height;
	public static String WebBrowserViewer_LBL_SetSize;
	public static String WebBrowserViewer_LBL_Stop;
	public static String WebBrowserViewer_LBL_Width;
	public static String WebBrowserViewer_TTP_Back;
	public static String WebBrowserViewer_TTP_Forward;
	public static String WebBrowserViewer_TTP_Go;
	public static String WebBrowserViewer_TTP_Refresh;
	public static String WebBrowserViewer_TTP_Stop;

	public static String WebBrowserViewer_WindowShellTitle;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
