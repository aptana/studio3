package com.aptana.scripting.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.scripting.model.messages"; //$NON-NLS-1$
	
	public static String BundleEntry_Name_Not_Defined;

	public static String BundleManager_BUNDLE_DIRECTORY_DOES_NOT_EXIST;
	public static String BundleManager_BUNDLE_FILE_NOT_A_DIRECTORY;

	public static String BundleManager_Executed_Null_Script;

	public static String BundleManager_No_Bundle_File;

	public static String BundleManager_Reloaded_Null_Script;
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
