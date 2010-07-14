package com.aptana.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.preferences.messages"; //$NON-NLS-1$

	public static String GenericRootPage_genericPerferencesPageMessage;
	public static String GenericRootPage_noAvailablePages;
	public static String GenericRootPage_preferences;

	public static String PropertyAndPreferencePage_configureProjectSettings;

	public static String PropertyAndPreferencePage_configureWorkspaceSettings;

	public static String PropertyAndPreferencePage_enableProjectSpecific;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
