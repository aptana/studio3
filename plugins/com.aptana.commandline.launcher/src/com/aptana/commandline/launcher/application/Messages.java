package com.aptana.commandline.launcher.application;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.commandline.launcher.application.messages"; //$NON-NLS-1$
	public static String LauncherApplication_ApplicationNotFound;
	public static String LauncherApplication_CouldNotSendCommandLineArguments;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
