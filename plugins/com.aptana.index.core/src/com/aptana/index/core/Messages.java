package com.aptana.index.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.index.core.messages"; //$NON-NLS-1$
	public static String IndexFilesOfProjectJob_Name;
	public static String IndexProjectJob_Name;
	public static String IndexRequestJob_Name;
	public static String RemoveIndexOfFilesOfProjectJob_Name;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
