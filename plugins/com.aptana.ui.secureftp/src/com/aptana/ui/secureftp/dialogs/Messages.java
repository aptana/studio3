/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.secureftp.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ui.secureftp.dialogs.messages"; //$NON-NLS-1$

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
