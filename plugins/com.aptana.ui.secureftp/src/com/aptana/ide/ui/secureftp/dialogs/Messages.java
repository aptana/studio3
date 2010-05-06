package com.aptana.ide.ui.secureftp.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.ui.secureftp.dialogs.messages"; //$NON-NLS-1$

	public static String CommonFTPConnectionPointPropertyDialog_ERR_PrivateKey;
	public static String CommonFTPConnectionPointPropertyDialog_IncorrectPassphrase;
	public static String CommonFTPConnectionPointPropertyDialog_NoPrivateKeySelected;
	public static String CommonFTPConnectionPointPropertyDialog_Passphrase;
	public static String CommonFTPConnectionPointPropertyDialog_Password;
	public static String CommonFTPConnectionPointPropertyDialog_Protocol;
	public static String CommonFTPConnectionPointPropertyDialog_SpecifyPrivateKey;
	public static String CommonFTPConnectionPointPropertyDialog_UsePublicKeyAuthentication;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
