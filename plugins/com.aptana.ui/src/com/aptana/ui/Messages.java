package com.aptana.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ui.messages"; //$NON-NLS-1$

	public static String DialogUtils_HideMessage;
	public static String UIUtils_Error;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
