package com.aptana.scripting.ui;
import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.scripting.ui.messages"; //$NON-NLS-1$
	public static String MenuDialog_NoMatchesFound;
	
	public static String EarlyStartup_SCRIPTING_CONSOLE_NAME;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
