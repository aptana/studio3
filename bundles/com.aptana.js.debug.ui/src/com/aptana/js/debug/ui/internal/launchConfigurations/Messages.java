/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.launchConfigurations;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.js.debug.ui.internal.launchConfigurations.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String LaunchBrowserSettingsTab_AppendProjectName;
	public static String LaunchBrowserSettingsTab_Arguments;
	public static String LaunchBrowserSettingsTab_WebBrowser;
	public static String LaunchBrowserSettingsTab_BrowserExecutable;
	public static String LaunchBrowserSettingsTab_ExecutableFiles;
	public static String LaunchBrowserSettingsTab_StartAction;
	public static String LaunchBrowserSettingsTab_Use_Selected_Server;
	public static String LaunchBrowserSettingsTab_UseCurrentPage;
	public static String LaunchBrowserSettingsTab_SpecificPage;
	public static String LaunchBrowserSettingsTab_StartURL;
	public static String LaunchBrowserSettingsTab_Server;
	public static String LaunchBrowserSettingsTab_ServerNotSelected;
	public static String LaunchBrowserSettingsTab_UseBuiltInWebServer;
	public static String LaunchBrowserSettingsTab_UseExternalWebServer;
	public static String LaunchBrowserSettingsTab_BaseURL;
	public static String LaunchBrowserSettingsTab_ChooseFile;
	public static String LaunchBrowserSettingsTab_Configure_Label;
	public static String LaunchBrowserSettingsTab_BrowserExecutableShouldBeSpecified;
	public static String LaunchBrowserSettingsTab_StartPageShouldBeSpecified;
	public static String LaunchBrowserSettingsTab_ValidStartPageURLShouldBeSpecified;
	public static String LaunchBrowserSettingsTab_ValidBaseURLShouldBeSpecified;
	public static String LaunchBrowserSettingsTab_NoFilesOpenedInEditor;
	public static String LaunchBrowserSettingsTab_Main;
	public static String LaunchServerSettingsTab_Error_ValidServerRequired;
	public static String LaunchServerSettingsTab_Host;
	public static String LaunchServerSettingsTab_Server;

	public static String DebugSettingsTab_UseLaunchSpecificOptions;
	public static String DebugSettingsTab_SuspendOptions;
	public static String DebugSettingsTab_SuspendAtStart;
	public static String DebugSettingsTab_SuspendOnDebuggerKeyword;
	public static String DebugSettingsTab_Debug;
	
	public static String AdvancedSettingsTab_EnableDebuggingInRunMode;
	public static String AdvancedSettingsTab_Advanced;

	public static String HttpServerSettingsTab_Title;
	public static String HttpSettingsTab_GET_query;
	public static String HttpSettingsTab_HTTP;
}
