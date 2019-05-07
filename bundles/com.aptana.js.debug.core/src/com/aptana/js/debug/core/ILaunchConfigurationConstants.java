/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core;

/**
 * Launch configuration constants
 */
public interface ILaunchConfigurationConstants
{
	String ID_JS_APPLICATION = "com.aptana.js.debug.core.webbrowserLaunchConfigurationType"; //$NON-NLS-1$

	String CONFIGURATION_SERVER_TYPE = "serverType"; //$NON-NLS-1$

	String CONFIGURATION_START_ACTION_TYPE = "startActionType"; //$NON-NLS-1$
	String CONFIGURATION_START_PAGE_PATH = "startPagePath"; //$NON-NLS-1$
	String CONFIGURATION_START_PAGE_URL = "startPageUrl"; //$NON-NLS-1$

	String CONFIGURATION_BROWSER_EXECUTABLE = "browserExecutable"; //$NON-NLS-1$
	String CONFIGURATION_BROWSER_NATURE = "browserNature"; //$NON-NLS-1$
	String CONFIGURATION_BROWSER_COMMAND_LINE = "browserCmdLine"; //$NON-NLS-1$

	String CONFIGURATION_SERVER_HOST = "serverHost"; //$NON-NLS-1$
	String CONFIGURATION_SERVER_NAME = "serverName"; //$NON-NLS-1$
	String CONFIGURATION_EXTERNAL_BASE_URL = "externalBaseUrl"; //$NON-NLS-1$

	String CONFIGURATION_APPEND_PROJECT_NAME = "appendProjectName"; //$NON-NLS-1$

	/**
	 * Attribute used to store the name of the project being launched.
	 */
	String ATTR_PROJECT_NAME = "ATTR_PROJECT_NAME"; //$NON-NLS-1$

	String CONFIGURATION_HTTP_GET_QUERY = "httpGetData"; //$NON-NLS-1$
	String CONFIGURATION_HTTP_POST_DATA = "httpPostData"; //$NON-NLS-1$
	String CONFIGURATION_HTTP_POST_CONTENT_TYPE = "httpPostContentType"; //$NON-NLS-1$

	String CONFIGURATION_ADVANCED_RUN_ENABLED = "advancedRunEnabled"; //$NON-NLS-1$

	int SERVER_INTERNAL = 1;
	int SERVER_MANAGED = 2;
	int SERVER_EXTERNAL = 3;

	int START_ACTION_CURRENT_PAGE = 1;
	int START_ACTION_SPECIFIC_PAGE = 2;
	int START_ACTION_START_URL = 3;

	int DEFAULT_SERVER_TYPE = SERVER_INTERNAL;

	int DEFAULT_START_ACTION_TYPE = START_ACTION_CURRENT_PAGE;

	String DEFAULT_BROWSER_WINDOWS_IE = "%ProgramFiles%\\Internet Explorer\\iexplore.exe"; //$NON-NLS-1$
	String[] DEFAULT_BROWSER_WINDOWS_FIREFOX = new String[] { "%ProgramFiles%\\Mozilla Firefox\\firefox.exe", //$NON-NLS-1$
			"%ProgramFiles(x86)%\\Mozilla Firefox\\firefox.exe" //$NON-NLS-1$
	};

	String DEFAULT_BROWSER_MACOSX_SAFARI = "/Applications/Safari.app"; //$NON-NLS-1$
	String[] DEFAULT_BROWSER_MACOSX_FIREFOX = new String[] { "/Applications/Firefox.app", //$NON-NLS-1$
			"~/Applications/Firefox.app" //$NON-NLS-1$
	};

	String[] DEFAULT_BROWSER_LINUX_FIREFOX = new String[] { "/usr/bin/firefox/firefox", //$NON-NLS-1$
			"/usr/bin/firefox", //$NON-NLS-1$
			"/usr/lib/firefox/firefox", //$NON-NLS-1$
			"/usr/local/firefox/firefox", //$NON-NLS-1$
			"/opt/firefox/firefox" //$NON-NLS-1$
	};
}
