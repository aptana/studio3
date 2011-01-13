/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.js.debug.ui.internal.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String InstallDebuggerPromptStatusHandler_Download;

	public static String InstallDebuggerPromptStatusHandler_ExtensionInstallFailed;

	public static String InstallDebuggerPromptStatusHandler_PDMNotInstalled;

	public static String LaunchDebuggerPromptStatusHandler_CloseActiveSession;

	public static String Startup_DontAskAgain;

	/**
	 * WorkbenchCloseListener_ConfirmDebuggerExit
	 */
	public static String WorkbenchCloseListener_ConfirmDebuggerExit;

	/**
	 * WorkbenchCloseListener_AptanaDebuggerIsActive_DoYouWantToExit
	 */
	public static String WorkbenchCloseListener_AptanaDebuggerIsActive_DoYouWantToExit;

	/**
	 * WorkbenchCloseListener_AlwaysExitDebuggerWithoutPrompt
	 */
	public static String WorkbenchCloseListener_AlwaysExitDebuggerWithoutPrompt;

	/**
	 * Startup_Notification
	 */
	public static String Startup_Notification;

	/**
	 * Startup_AptanaRequiresFirefox
	 */
	public static String Startup_AptanaRequiresFirefox;

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
}
