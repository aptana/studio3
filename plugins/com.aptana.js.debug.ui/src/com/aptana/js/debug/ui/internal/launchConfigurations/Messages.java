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
package com.aptana.js.debug.ui.internal.launchConfigurations;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.js.debug.ui.internal.launchConfigurations.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String HttpServerSettingsTab_Add;

	public static String HttpServerSettingsTab_AddNewPath;

	public static String HttpServerSettingsTab_AddServerPath;

	public static String HttpServerSettingsTab_Edit;

	public static String HttpServerSettingsTab_EditPath;

	public static String HttpServerSettingsTab_EditSelectedPath;

	public static String HttpServerSettingsTab_Remove;

	public static String HttpServerSettingsTab_RemoveSelectedPath;

	public static String HttpServerSettingsTab_ServerPath;

	public static String HttpServerSettingsTab_Title;

	public static String HttpServerSettingsTab_WebServerPathConfiguration;

	public static String HttpServerSettingsTab_WorkspacePath;

	public static String LaunchBrowserSettingsTab_AppendProjectName;

	/**
	 * LaunchBrowserSettingsTab_Arguments
	 */
	public static String LaunchBrowserSettingsTab_Arguments;

	/**
	 * LaunchBrowserSettingsTab_WebBrowser
	 */
	public static String LaunchBrowserSettingsTab_WebBrowser;

	/**
	 * LaunchBrowserSettingsTab_BrowserExecutable
	 */
	public static String LaunchBrowserSettingsTab_BrowserExecutable;

	/**
	 * LaunchBrowserSettingsTab_ExecutableFiles
	 */
	public static String LaunchBrowserSettingsTab_ExecutableFiles;

	/**
	 * LaunchBrowserSettingsTab_StartAction
	 */
	public static String LaunchBrowserSettingsTab_StartAction;

	public static String LaunchBrowserSettingsTab_Use_Selected_Server;

	/**
	 * LaunchBrowserSettingsTab_UseCurrentPage
	 */
	public static String LaunchBrowserSettingsTab_UseCurrentPage;

	/**
	 * LaunchBrowserSettingsTab_SpecificPage
	 */
	public static String LaunchBrowserSettingsTab_SpecificPage;

	/**
	 * LaunchBrowserSettingsTab_StartURL
	 */
	public static String LaunchBrowserSettingsTab_StartURL;

	/**
	 * LaunchBrowserSettingsTab_Server
	 */
	public static String LaunchBrowserSettingsTab_Server;

	public static String LaunchBrowserSettingsTab_ServerNotSelected;

	/**
	 * LaunchBrowserSettingsTab_UseBuiltInWebServer
	 */
	public static String LaunchBrowserSettingsTab_UseBuiltInWebServer;

	/**
	 * LaunchBrowserSettingsTab_UseExternalWebServer
	 */
	public static String LaunchBrowserSettingsTab_UseExternalWebServer;

	/**
	 * LaunchBrowserSettingsTab_BaseURL
	 */
	public static String LaunchBrowserSettingsTab_BaseURL;

	/**
	 * LaunchBrowserSettingsTab_ChooseFile
	 */
	public static String LaunchBrowserSettingsTab_ChooseFile;

	/**
	 * LaunchBrowserSettingsTab_BrowserExecutableShouldBeSpecified
	 */
	public static String LaunchBrowserSettingsTab_BrowserExecutableShouldBeSpecified;

	/**
	 * LaunchBrowserSettingsTab_StartPageShouldBeSpecified
	 */
	public static String LaunchBrowserSettingsTab_StartPageShouldBeSpecified;

	/**
	 * LaunchBrowserSettingsTab_ValidStartPageURLShouldBeSpecified
	 */
	public static String LaunchBrowserSettingsTab_ValidStartPageURLShouldBeSpecified;

	/**
	 * LaunchBrowserSettingsTab_ValidBaseURLShouldBeSpecified
	 */
	public static String LaunchBrowserSettingsTab_ValidBaseURLShouldBeSpecified;

	/**
	 * LaunchBrowserSettingsTab_NoFilesOpenedInEditor
	 */
	public static String LaunchBrowserSettingsTab_NoFilesOpenedInEditor;

	/**
	 * LaunchBrowserSettingsTab_Main
	 */
	public static String LaunchBrowserSettingsTab_Main;

	/**
	 * DebugSettingsTab_UseLaunchSpecificOptions
	 */
	public static String DebugSettingsTab_UseLaunchSpecificOptions;

	/**
	 * DebugSettingsTab_SuspendOptions
	 */
	public static String DebugSettingsTab_SuspendOptions;

	/**
	 * DebugSettingsTab_SuspendAtStart
	 */
	public static String DebugSettingsTab_SuspendAtStart;

	/**
	 * DebugSettingsTab_SuspendOnExceptions
	 */
	public static String DebugSettingsTab_SuspendOnExceptions;

	/**
	 * DebugSettingsTab_SuspendOnErrors
	 */
	public static String DebugSettingsTab_SuspendOnErrors;

	/**
	 * DebugSettingsTab_SuspendOnDebuggerKeyword
	 */
	public static String DebugSettingsTab_SuspendOnDebuggerKeyword;

	/**
	 * DebugSettingsTab_Debug
	 */
	public static String DebugSettingsTab_Debug;

	/**
	 * AdvancedSettingsTab_EnableDebuggingInRunMode
	 */
	public static String AdvancedSettingsTab_EnableDebuggingInRunMode;

	/**
	 * AdvancedSettingsTab_Advanced
	 */
	public static String AdvancedSettingsTab_Advanced;

	/**
	 * HttpSettingsTab_GET_query
	 */
	public static String HttpSettingsTab_GET_query;

	/**
	 * HttpSettingsTab_HTTP
	 */
	public static String HttpSettingsTab_HTTP;

	public static String LaunchServerSettingsTab_Error_ValidServerRequired;

	public static String LaunchServerSettingsTab_Host;

	public static String LaunchServerSettingsTab_Server;
}
