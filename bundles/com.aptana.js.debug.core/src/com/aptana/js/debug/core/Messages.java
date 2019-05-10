/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.js.debug.core.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String JSLaunchConfigurationDelegate_MultipleJavaScriptDebugNotSupported;
	public static String JSLaunchConfigurationDelegate_PleaseTerminateActiveSession;
	public static String JSLaunchConfigurationDelegate_WebBrowserDoesNotExist;
	public static String JSLaunchConfigurationDelegate_MalformedServerURL;
	public static String JSLaunchConfigurationDelegate_LaunchingHTTPServer;
	public static String JSLaunchConfigurationDelegate_MalformedLaunchURL;
	public static String JSLaunchConfigurationDelegate_LaunchURLNotDefined;
	public static String JSLaunchConfigurationDelegate_LaunchingBrowser;
	public static String JSLaunchConfigurationDelegate_Only_Project_Debugging_Supported;
	public static String JSLaunchConfigurationDelegate_OpeningSocketOnPort;
	public static String JSLaunchConfigurationDelegate_ServerNotFound0_Error;
	public static String JSLaunchConfigurationDelegate_SocketConnectionError;
	public static String JSLaunchConfigurationDelegate_InitializingDebugger;
	public static String JSLaunchConfigurationDelegate_OpeningPage;
	public static String JSLaunchConfigurationDelegate_LaunchProcessError;
	public static String JSLaunchConfigurationDelegate_ConfiguredBrowserDoesNotSupportDebugging;
	public static String JSLaunchConfigurationDelegate_Empty_URL;
	public static String JSLaunchConfigurationDelegate_No_Server_Type;
	public static String JSLaunchConfigurationHelper_Empty_Server_URL;
	public static String JSLaunchConfigurationHelper_Malformed_URL;

	public static String JSRemoteLaunchConfigurationDelegate_ConnectingServer;
	public static String JSRemoteLaunchConfigurationDelegate_Server;
}
