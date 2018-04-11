/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core;

import org.eclipse.osgi.util.NLS;

/**
 * Messages class for internationalization
 * 
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.core.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String CorePlugin_Adding_Unified_Builders;
	public static String CorePlugin_Hooking_Filewatchers;
	public static String CoreStrings_Add;
	public static String CoreStrings_Browse;
	public static String CoreStrings_Delete;
	public static String CoreStrings_Edit;
	public static String CoreStrings_Error;
	public static String CoreStrings_New;
	public static String CoreStrings_Open;
	public static String CoreStrings_Properties;
	public static String CoreStrings_Refresh;
	public static String CoreStrings_Remove;
	public static String CoreStrings_Rename;
	public static String CoreStrings_Continue;
	public static String CoreStrings_Cancel;
	public static String CoreStrings_Help;
	public static String CoreStrings_On;
	public static String CoreStrings_Off;

	public static String CorePlugin_MD5_generation_error;

	public static String IProblem_Error;
	public static String IProblem_Ignore;
	public static String IProblem_Info;
	public static String IProblem_Warning;

}
