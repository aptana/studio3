package com.aptana.ide.ui.ftp.console;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.ui.ftp.console.messages"; //$NON-NLS-1$

	public static String FTPConsoleFactory_FTPConsole;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
