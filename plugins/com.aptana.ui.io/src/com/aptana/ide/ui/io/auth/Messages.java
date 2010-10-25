package com.aptana.ide.ui.io.auth;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.ui.io.auth.messages"; //$NON-NLS-1$

	public static String PasswordPromptDialog_Login;
	public static String PasswordPromptDialog_Password;
	public static String PasswordPromptDialog_SavePassword;
	public static String PasswordPromptDialog_UserName;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
