package com.aptana.deploy;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$

	public static String HerokuAPI_UnableToGetHerokuCredentialsError;
	public static String HerokuAPI_AuthFailed_Error;
	public static String HerokuAPI_AuthConnectionFailed_Error;
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
