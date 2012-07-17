package com.aptana.core.build;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.core.build.messages"; //$NON-NLS-1$
	public static String IProblem_Error;
	public static String IProblem_Ignore;
	public static String IProblem_Info;
	public static String IProblem_Warning;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
