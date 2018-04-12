/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.secureftp.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ui.secureftp.internal.messages"; //$NON-NLS-1$

	public static String FTPSAdvancedOptionsComposite_ConnectMode;
	public static String FTPSAdvancedOptionsComposite_Detect;
	public static String FTPSAdvancedOptionsComposite_Encoding;
	public static String FTPSAdvancedOptionsComposite_InvalidPort;
	public static String FTPSAdvancedOptionsComposite_MethodExplicit;
	public static String FTPSAdvancedOptionsComposite_MethodImplicit;
	public static String FTPSAdvancedOptionsComposite_ModeActive;
	public static String FTPSAdvancedOptionsComposite_ModePassive;

	public static String FTPSAdvancedOptionsComposite_NoSSLSessionResumption;
	public static String FTPSAdvancedOptionsComposite_Port;
	public static String FTPSAdvancedOptionsComposite_SSLMethod;
	public static String FTPSAdvancedOptionsComposite_Timezone;
	public static String FTPSAdvancedOptionsComposite_ValidateCertificate;
	public static String SFTPAdvancedOptionsComposite_Compression;
	public static String SFTPAdvancedOptionsComposite_Encoding;
	public static String SFTPAdvancedOptionsComposite_InvalidPort;
	public static String SFTPAdvancedOptionsComposite_Port;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
