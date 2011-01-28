/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.scripting.messages"; //$NON-NLS-1$
	public static String EarlyStartup_Error_Initializing_File_Monitoring;
	public static String ScriptingEngine_Error_Executing_Script;
	public static String ScriptingEngine_Error_Setting_JRuby_Home;
	public static String ScriptingEngine_Execution_Error;
	public static String ScriptingEngine_Parse_Error;
	public static String ScriptingEngine_Unable_To_Convert_Load_Path;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
