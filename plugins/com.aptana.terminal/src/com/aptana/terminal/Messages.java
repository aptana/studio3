package com.aptana.terminal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.terminal.messages"; //$NON-NLS-1$
	public static String Utils_Unable_To_Open_Editor;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
