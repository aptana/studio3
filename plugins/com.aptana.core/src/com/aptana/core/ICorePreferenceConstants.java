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
	 * A boolean used to swap system jobs to user so they show in the UI for debugging CPU usage.
	 */
	String PREF_SHOW_SYSTEM_JOBS = "show_system_jobs"; //$NON-NLS-1$

	/**
	 * A boolean use to enable the migration of existing Studio 2.x projects to Studio 3
	 */
	String PREF_AUTO_MIGRATE_OLD_PROJECTS = "auto_migrate_old_projects"; //$NON-NLS-1$

	/**
	 * A boolean to enable/disable attaching filewatchers to automatically refresh/handle edits made to project's
	 * contents outside the IDE.
	 */
	String PREF_AUTO_REFRESH_PROJECTS = "auto_refresh_projects"; //$NON-NLS-1$

}
