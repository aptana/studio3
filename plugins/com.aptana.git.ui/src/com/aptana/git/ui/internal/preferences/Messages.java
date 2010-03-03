package com.aptana.git.ui.internal.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.preferences.messages"; //$NON-NLS-1$
	public static String GitExecutableLocationPage_CalculatePullIndicatorLabel;
	public static String GitExecutableLocationPage_InvalidLocationErrorMessage;
	public static String GitExecutableLocationPage_LocationLabel;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
