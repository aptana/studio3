package com.aptana.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.preferences.messages"; //$NON-NLS-1$

	public static String GenericRootPage_genericPerferencesPageMessage;
	public static String GenericRootPage_noAvailablePages;
	public static String GenericRootPage_preferences;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
