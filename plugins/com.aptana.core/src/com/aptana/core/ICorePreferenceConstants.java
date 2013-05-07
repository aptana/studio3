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
	public static final boolean DEFAULT_DEBUG_MODE = false;

	/**
	 * A boolean to enable/disable attaching filewatchers to automatically refresh/handle edits made to project's
	 * contents outside the IDE.
	 */
	public static final boolean DEFAULT_AUTO_REFRESH_PROJECTS = true;

	/**
	 * The current level of debugging
	 */
	String PREF_DEBUG_LEVEL = "pref_debug_level"; //$NON-NLS-1$

	/**
	 * Are we debugging all components, or jsut some of them
	 */
	String PREF_ENABLE_COMPONENT_DEBUGGING = "pref_enable_component_debugging"; //$NON-NLS-1$

	/**
	 * The list of components to debug
	 */
	String PREF_DEBUG_COMPONENT_LIST = "pref_debug_component_list"; //$NON-NLS-1$

	/**
	 * The preference key for the comma-separated list of task tag names.
	 */
	public static final String TASK_TAG_NAMES = "com.aptana.editor.common.taskTagNames"; //$NON-NLS-1$

	/**
	 * The preference key for the comma-separated list of task tag priorities. Order is important and lines up with
	 * {@value #TASK_TAG_NAMES}
	 */
	public static final String TASK_TAG_PRIORITIES = "com.aptana.editor.common.taskTagPriorities"; //$NON-NLS-1$

	/**
	 * The preference key for determining if task tags should be treated in a case-sensitive manner when detecting them.
	 */
	public static final String TASK_TAGS_CASE_SENSITIVE = "com.aptana.editor.common.taskTagsCaseSensitive"; //$NON-NLS-1$

}
