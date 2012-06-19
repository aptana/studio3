/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.scripting.model.messages"; //$NON-NLS-1$

	public static String BundleCacher_ComparingTimestampSubTaskName;
	public static String BundleCacher_FileReferencedInCacheMissingMsg;
	public static String BundleCacher_LoadCacheTaskName;
	public static String BundleCacher_LoadingYAMLError;
	public static String BundleCacher_OutOfDateCacheMsg;
	public static String BundleCacher_SerializationExceptionDeletingCacheFile;

	public static String BundleEntry_Name_Not_Defined;

	public static String BundleManager_Bundle_Became_Hidden_Event_Error;
	public static String BundleManager_Bundle_Became_Visible_Event_Error;
	public static String BundleManager_Element_Became_Hidden_Event_Error;
	public static String BundleManager_Element_Became_Visible_Event_Error;
	public static String BundleManager_Executed_Null_Script;
	public static String BundleManager_ProjectBundlesInBundlesDirectoryIsDeprecated;
	public static String BundleManager_Reloaded_Null_Script;
	public static String BundleManager_Script_Loaded_Event_Error;
	public static String BundleManager_Script_Reloaded_Event_Error;
	public static String BundleManager_Script_Unloaded_Event_Error;
	public static String BundleManager_Unloaded_Null_Script;
	public static String BundleManager_UNREADABLE_SCRIPT;
	public static String BundleManager_USER_PATH_NOT_DIRECTORY;
	public static String BundleManager_USER_PATH_NOT_READ_WRITE;

	public static String BundleMonitor_Error_Processing_Resource_Change;
	public static String BundleMonitor_ERROR_REGISTERING_FILE_WATCHER;
	public static String BundleMonitor_INVALID_WATCHER_PATH;

	public static String CommandElement_Error_Building_Env_Variables;
	public static String CommandElement_Error_Creating_Contributor;
	public static String CommandElement_Error_Executing_Command;
	public static String CommandElement_Error_Processing_Command_Block;
	public static String CommandElement_Invalid_Key_Binding;
	public static String CommandElement_Undefined_Key_Binding;
	public static String CommandElement_Unrecognized_OS;

	public static String CommandScriptRunner_CANNOT_LOCATE_SHELL;
	public static String CommandScriptRunner_UNABLE_TO_LOCATE_SHELL_FOR_COMMAND;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
