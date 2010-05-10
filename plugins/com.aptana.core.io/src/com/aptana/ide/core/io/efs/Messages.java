package com.aptana.ide.core.io.efs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.core.io.efs.messages"; //$NON-NLS-1$

	public static String VirtualFile_ListingDirectory;
	public static String VirtualFileSystem_ERR_FetchFileTree;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
