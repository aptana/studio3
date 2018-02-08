package com.aptana.ui.handlers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.handlers.messages"; //$NON-NLS-1$
	public static String AppcSocketMessagesHandler_Description;
	public static String AppcSocketMessagesHandler_title;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
