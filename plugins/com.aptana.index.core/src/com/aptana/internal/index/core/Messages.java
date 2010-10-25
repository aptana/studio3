package com.aptana.internal.index.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.internal.index.core.messages"; //$NON-NLS-1$
	public static String DiskIndex_Unable_To_Create_Index_File;
	public static String DiskIndex_Wrong_Format;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
