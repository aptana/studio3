package com.aptana.portal.ui.dispatch.browserFunctions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.portal.ui.dispatch.browserFunctions.messages"; //$NON-NLS-1$
	public static String DispatcherBrowserFunction_expectedJSONMap;
	public static String DispatcherBrowserFunction_unknownAction;
	public static String DispatcherBrowserFunction_unknownController;
	public static String DispatcherBrowserFunction_wrongOrMissingArguments;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
