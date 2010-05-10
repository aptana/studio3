package com.aptana.ide.core.io.auth;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.core.io.auth.messages"; //$NON-NLS-1$

	public static String AuthenticationManager_FailedGetSecurePreference;
	public static String AuthenticationManager_FailedSaveSecurePreference;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
