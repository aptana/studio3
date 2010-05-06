package com.aptana.terminal.internal.handlers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.terminal.internal.handlers.messages"; //$NON-NLS-1$

	public static String ShowTerminalHandler_ERR_OpeningTerminal;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
