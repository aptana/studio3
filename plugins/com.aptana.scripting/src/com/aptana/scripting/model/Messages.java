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

	public static String BundleMonitor_Error_Processing_Resource_Change;

	public static String CommandElement_Error_Creating_Contributor;

	public static String CommandElement_Error_Processing_Command_Block;

	public static String CommandElement_Invalid_Key_Binding;
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
