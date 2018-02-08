/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.logging;

import org.eclipse.osgi.util.NLS;

/**
 * Messages class for internationalization
 * @author Ingo Muschenetz
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.core.logging.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	public static String IdeLog_ERROR;

	public static String IdeLog_File_Written_To;

	public static String IdeLog_WARNING;

	public static String IdeLog_INFO;

	/**
	 * IdeLog_LogMessage
	 */
	public static String IdeLog_LogMessage;

	public static String IdeLog_Unable_To_Write_Temporary_File;

	public static String IdeLog_UNKNOWN;

}
