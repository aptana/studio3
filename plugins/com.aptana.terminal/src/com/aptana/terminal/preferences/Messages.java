package com.aptana.terminal.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.terminal.preferences.messages"; //$NON-NLS-1$
	public static String TerminalPreferencePage_Close_View_On_Exit;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
