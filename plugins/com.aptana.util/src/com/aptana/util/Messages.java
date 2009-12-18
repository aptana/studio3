package com.aptana.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.util.messages"; //$NON-NLS-1$
	public static String ResourceUtils_File_URL_To_URI_Conversion_Error;
	public static String ResourceUtils_URL_To_File_URL_Conversion_Error;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
