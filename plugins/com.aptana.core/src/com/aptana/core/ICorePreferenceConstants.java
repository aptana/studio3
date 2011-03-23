/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core;

/**
 * @author Max Stepanov
 */
public interface ICorePreferenceConstants
{

	String PREF_SHELL_EXECUTABLE_PATH = "shell_executable_path"; //$NON-NLS-1$
	String PREF_WEB_FILES = "web_files"; //$NON-NLS-1$

	/**
	 * A boolean used to turn on our own "debug" mode. Meant to be used to swap system jobs to user so they show in the
	 * UI for debugging CPU usage.
	 */
	String PREF_DEBUG_MODE = "debug_mode"; //$NON-NLS-1$
}
