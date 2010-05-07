package com.aptana.git.core.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.core.model.messages"; //$NON-NLS-1$
	
	public static String GitIndex_BinaryDiff_Message;
	public static String GitRepositoryManager_UnableToFindGitExecutableError;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
