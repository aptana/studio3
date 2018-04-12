/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.js.debug.ui.internal.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String InstallDebuggerPromptStatusHandler_Download;

	public static String InstallDebuggerPromptStatusHandler_ExtensionInstallFailed;

	public static String InstallDebuggerPromptStatusHandler_PDMNotInstalled;

	public static String LaunchDebuggerPromptStatusHandler_CloseActiveSession;

	public static String StartPageManager_DefaultConfigurationName;

	public static String Startup_DontAskAgain;

	/**
	 * WorkbenchCloseListener_ConfirmDebuggerExit
	 */
	public static String WorkbenchCloseListener_ConfirmDebuggerExit;

	/**
	 * WorkbenchCloseListener_DebuggerIsActive_DoYouWantToExit
	 */
	public static String WorkbenchCloseListener_DebuggerIsActive_DoYouWantToExit;

	/**
	 * WorkbenchCloseListener_AlwaysExitDebuggerWithoutPrompt
	 */
	public static String WorkbenchCloseListener_AlwaysExitDebuggerWithoutPrompt;

	/**
	 * Startup_Notification
	 */
	public static String Startup_Notification;

	/**
	 * Startup_StudioRequiresFirefox
	 */
	public static String Startup_StudioRequiresFirefox;

	/**
	 * Startup_Download
	 */
	public static String Startup_Download;

	/**
	 * Startup_CheckAgain
	 */
	public static String Startup_CheckAgain;

	/**
	 * Startup_ExecutableFiles
	 */
	public static String Startup_ExecutableFiles;

	/**
	 * LaunchDebuggerPromptStatusHandler_Title
	 */
	public static String LaunchDebuggerPromptStatusHandler_Title;

	/**
	 * LaunchDebuggerPromptStatusHandler_DebuggerSessionIsActive
	 */
	public static String LaunchDebuggerPromptStatusHandler_DebuggerSessionIsActive;

	/**
	 * JSDebugModelPresentation_line
	 */
	public static String JSDebugModelPresentation_line;

	/**
	 * JSDebugModelPresentation_notavailable
	 */
	public static String JSDebugModelPresentation_notavailable;

	/**
	 * JSDebugModelPresentation_Terminated
	 */
	public static String JSDebugModelPresentation_Terminated;

	/**
	 * JSDebugModelPresentation_Suspended
	 */
	public static String JSDebugModelPresentation_Suspended;

	/**
	 * JSDebugModelPresentation_lineIn_0_1_2
	 */
	public static String JSDebugModelPresentation_lineIn_0_1_2;

	/**
	 * JSDebugModelPresentation_keywordAtLine_0_1_2
	 */
	public static String JSDebugModelPresentation_keywordAtLine_0_1_2;

	/**
	 * JSDebugModelPresentation_atStartLine_0_1_2
	 */
	public static String JSDebugModelPresentation_atStartLine_0_1_2;

	/**
	 * JSDebugModelPresentation_exceptionAtLine_0_1_2
	 */
	public static String JSDebugModelPresentation_exceptionAtLine_0_1_2;

	/**
	 * JSDebugModelPresentation_watchpointAtLine_0_1_2
	 */
	public static String JSDebugModelPresentation_watchpointAtLine_0_1_2;

	/**
	 * JSDebugModelPresentation_runToLine_0_1_2
	 */
	public static String JSDebugModelPresentation_runToLine_0_1_2;

	/**
	 * JSDebugModelPresentation_breakpointAtLine_0_1_2
	 */
	public static String JSDebugModelPresentation_breakpointAtLine_0_1_2;

	/**
	 * JSDebugModelPresentation_Stepping
	 */
	public static String JSDebugModelPresentation_Stepping;

	/**
	 * JSDebugModelPresentation_Running
	 */
	public static String JSDebugModelPresentation_Running;

	/**
	 * JSDebugModelPresentation_Exception
	 */
	public static String JSDebugModelPresentation_Exception;

	/**
	 * JSDebugModelPresentation_UnknownName
	 */
	public static String JSDebugModelPresentation_UnknownName;

	/**
	 * JSDebugModelPresentation_UnknownType
	 */
	public static String JSDebugModelPresentation_UnknownType;

	/**
	 * JSDebugModelPresentation_UnknownValue
	 */
	public static String JSDebugModelPresentation_UnknownValue;

	/**
	 * JSDebugModelPresentation_DetailsComputing
	 */
	public static String JSDebugModelPresentation_DetailsComputing;

	/**
	 * InstallDebuggerPromptStatusHandler_InstallDebuggerExtension
	 */
	public static String InstallDebuggerPromptStatusHandler_InstallDebuggerExtension;

	/**
	 * InstallDebuggerPromptStatusHandler_WaitbrowserLaunches_AcceptExtensionInstallation_Quit
	 */
	public static String InstallDebuggerPromptStatusHandler_WaitbrowserLaunches_AcceptExtensionInstallation_Quit;

	/**
	 * InstallDebuggerPromptStatusHandler_WaitbrowserLaunches_Quit
	 */
	public static String InstallDebuggerPromptStatusHandler_WaitbrowserLaunches_Quit;

	/**
	 * InstallDebuggerPromptStatusHandler_BrowserIsRunning
	 */
	public static String InstallDebuggerPromptStatusHandler_BrowserIsRunning;

	/**
	 * InstallDebuggerPromptStatusHandler_ExtensionInstalled
	 */
	public static String InstallDebuggerPromptStatusHandler_ExtensionInstalled;

	/**
	 * InstallDebuggerPromptStatusHandler_ExtensionNotInstalled
	 */
	public static String InstallDebuggerPromptStatusHandler_ExtensionNotInstalled;

	public static String WorkbenchCloseListener_ConfirmProfilerExit;
	public static String WorkbenchCloseListener_ProfilerIsActive_DoYouWantToExit;
	public static String WorkbenchCloseListener_AlwaysExitProfilerWithoutPrompt;
}
