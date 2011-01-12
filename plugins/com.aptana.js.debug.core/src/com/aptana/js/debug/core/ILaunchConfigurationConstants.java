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
package com.aptana.js.debug.core;

/**
 * Launch configuration constants
 */
public interface ILaunchConfigurationConstants {
	/**
	 * ID_JS_APPLICATION
	 */
	String ID_JS_APPLICATION = "com.aptana.debug.core.jsLaunchConfigurationType"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_SERVER_TYPE
	 */
	String CONFIGURATION_SERVER_TYPE = "serverType"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_START_ACTION_TYPE
	 */
	String CONFIGURATION_START_ACTION_TYPE = "startActionType"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_START_PAGE_PATH
	 */
	String CONFIGURATION_START_PAGE_PATH = "startPagePath"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_START_PAGE_URL
	 */
	String CONFIGURATION_START_PAGE_URL = "startPageUrl"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_BROWSER_EXECUTABLE
	 */
	String CONFIGURATION_BROWSER_EXECUTABLE = "browserExecutable"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_BROWSER_NATURE
	 */
	String CONFIGURATION_BROWSER_NATURE = "browserNature"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_BROWSER_COMMAND_LINE
	 */
	String CONFIGURATION_BROWSER_COMMAND_LINE = "browserCmdLine"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_SERVER_HOST
	 */
	String CONFIGURATION_SERVER_HOST = "serverHost"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_SERVER_ID
	 */
	String CONFIGURATION_SERVER_ID = "serverName"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_EXTERNAL_BASE_URL
	 */
	String CONFIGURATION_EXTERNAL_BASE_URL = "externalBaseUrl"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_INCLUDE_PROJECT_NAME
	 */
	String CONFIGURATION_APPEND_PROJECT_NAME = "appendProjectName"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_HTTP_GET_QUERY
	 */
	String CONFIGURATION_HTTP_GET_QUERY = "httpGetData"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_HTTP_POST_DATA
	 */
	String CONFIGURATION_HTTP_POST_DATA = "httpPostData"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_HTTP_POST_CONTENT_TYPE
	 */
	String CONFIGURATION_HTTP_POST_CONTENT_TYPE = "httpPostContentType"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_SERVER_PATHS_MAPPING
	 */
	String CONFIGURATION_SERVER_PATHS_MAPPING = "pathsMapping"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_OVERRIDE_DEBUG_PREFERENCES
	 */
	String CONFIGURATION_OVERRIDE_DEBUG_PREFERENCES = "overrideDebugPreferences"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_SUSPEND_ON_FIRST_LINE
	 */
	String CONFIGURATION_SUSPEND_ON_FIRST_LINE = "suspendOnFirstLine"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_SUSPEND_ON_EXCEPTIONS
	 */
	String CONFIGURATION_SUSPEND_ON_EXCEPTIONS = "suspendOnExceptions"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_SUSPEND_ON_ERRORS
	 */
	String CONFIGURATION_SUSPEND_ON_ERRORS = "suspendOnErrors"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS
	 */
	String CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS = "suspendOnDebuggerKeywords"; //$NON-NLS-1$

	/**
	 * CONFIGURATION_ADVANCED_RUN_ENABLED
	 */
	String CONFIGURATION_ADVANCED_RUN_ENABLED = "advancedRunEnabled"; //$NON-NLS-1$

	/**
	 * SERVER_INTERNAL
	 */
	int SERVER_INTERNAL = 1;

	/**
	 * SERVER_EXTERNAL
	 */
	int SERVER_EXTERNAL = 2;

	/**
	 * SERVER_MANAGED
	 */
	int SERVER_MANAGED = 3;

	/**
	 * START_ACTION_CURRENT_PAGE
	 */
	int START_ACTION_CURRENT_PAGE = 1;

	/**
	 * START_ACTION_SPECIFIC_PAGE
	 */
	int START_ACTION_SPECIFIC_PAGE = 2;

	/**
	 * START_ACTION_START_URL
	 */
	int START_ACTION_START_URL = 3;

	/**
	 * DEFAULT_SERVER_TYPE
	 */
	int DEFAULT_SERVER_TYPE = SERVER_INTERNAL;

	/**
	 * DEFAULT_START_ACTION_TYPE
	 */
	int DEFAULT_START_ACTION_TYPE = START_ACTION_CURRENT_PAGE;

	/**
	 * DEFAULT_BROWSER_WINDOWS_IE
	 */
	String DEFAULT_BROWSER_WINDOWS_IE = "%ProgramFiles%\\Internet Explorer\\iexplore.exe"; //$NON-NLS-1$

	/**
	 * DEFAULT_BROWSER_WINDOWS_FIREFOX
	 */
	String[] DEFAULT_BROWSER_WINDOWS_FIREFOX = new String[] {
			"%ProgramFiles%\\Mozilla Firefox\\firefox.exe", //$NON-NLS-1$
			"%ProgramFiles(x86)%\\Mozilla Firefox\\firefox.exe" //$NON-NLS-1$
	};

	/**
	 * DEFAULT_BROWSER_MACOSX_SAFARI
	 */
	String DEFAULT_BROWSER_MACOSX_SAFARI = "/Applications/Safari.app"; //$NON-NLS-1$

	/**
	 * DEFAULT_BROWSER_MACOSX_FIREFOX
	 */
	String[] DEFAULT_BROWSER_MACOSX_FIREFOX = new String[] {
			"/Applications/Firefox.app", //$NON-NLS-1$
			"~/Applications/Firefox.app" //$NON-NLS-1$
	};

	/**
	 * DEFAULT_BROWSER_LINUX_FIREFOX
	 */
	String[] DEFAULT_BROWSER_LINUX_FIREFOX = new String[] {
			"/usr/bin/firefox/firefox", //$NON-NLS-1$
			"/usr/bin/firefox", //$NON-NLS-1$
			"/usr/lib/firefox/firefox", //$NON-NLS-1$
			"/usr/local/firefox/firefox", //$NON-NLS-1$
			"/opt/firefox/firefox" //$NON-NLS-1$
	};
}
