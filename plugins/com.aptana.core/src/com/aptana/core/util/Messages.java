/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.core.util.messages"; //$NON-NLS-1$
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}

	public static String IOUtil_Copy_Label;

	public static String IOUtil_Destination_Directory_Not_Writable;

	public static String IOUtil_Destination_Directory_Uncreatable;

	public static String IOUtil_Destination_Is_Not_A_Directory;

	public static String IOUtil_Directory_Copy_Error;

	public static String IOUtil_Source_Directory_Not_Readable;

	public static String IOUtil_Source_Not_Directory_Error;

	public static String IOUtil_Unable_To_Copy_Because;

	public static String ResourceUtils_File_URL_To_URI_Conversion_Error;

	public static String ResourceUtils_URL_To_File_URL_Conversion_Error;

	public static String PlatformUtils_CoreLibraryNotFound;

	public static String ProcessUtil_RunningProcess;

	public static String URLEncoder_Cannot_Encode_URL;

	public static String URLUtil_EvenNumberUrlParameters;

	public static String ZipUtil_ConflictsError;

	public static String ZipUtil_default_extract_label;

	public static String ZipUtil_ERR_NoWritePermission;

	public static String ZipUtil_extract_prefix_label;

}
