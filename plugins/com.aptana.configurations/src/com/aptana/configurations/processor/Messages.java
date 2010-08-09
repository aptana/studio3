package com.aptana.configurations.processor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.configurations.processor.messages"; //$NON-NLS-1$
	public static String AbstractConfigurationProcessor_expectedArrayError;
	public static String AbstractConfigurationProcessor_expectedMapError;
	public static String AbstractConfigurationProcessor_expectedURLsArrayError;
	public static String AbstractConfigurationProcessor_emptyURLsArrayError;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
