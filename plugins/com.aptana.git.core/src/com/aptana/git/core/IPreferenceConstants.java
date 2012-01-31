/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

public interface IPreferenceConstants
{

	/**
	 * Preference key for location of git executable to use.
	 */
	String GIT_EXECUTABLE_PATH = "git_executable_path"; //$NON-NLS-1$

	/**
	 * Preference key for determining if we should perform actual fetches for pull indicator calculations.
	 */
	String GIT_CALCULATE_PULL_INDICATOR = "git_calculate_pull_indicator"; //$NON-NLS-1$

	/**
	 * Preference key for boolean pref determining if we auto-attach our git provider to projects with git repos when
	 * the project gets added.
	 */
	String AUTO_ATTACH_REPOS = "auto_attach_repos"; //$NON-NLS-1$

	/**
	 * Preference key for boolean pref determining if we ignore when no git was found
	 */
	String IGNORE_NO_GIT = "ignore_no_git"; //$NON-NLS-1$

	/**
	 * If a resource inside the workspace changes, do we force an async refresh of the git repo it's in? This may not be
	 * necessary at all if we're already watching the git's index file via filewatcher.
	 */
	String REFRESH_INDEX_WHEN_RESOURCES_CHANGE = "refresh_git_index_when_resources_change"; //$NON-NLS-1$

}
