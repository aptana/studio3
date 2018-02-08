/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.security.internal.linux;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.security.internal.linux.messages"; //$NON-NLS-1$

	public static String messageEmptyPassword;
	public static String messageNoMatch;
	public static String buttonLogin;
	public static String buttonExit;
	public static String generalDialogTitle;
	public static String passwordChangeTitle;
	public static String messageLoginChange;
	public static String dialogTitle;
	public static String labelPassword;
	public static String labelConfirm;
	public static String showPassword;

	public static String PasswordProvider_ERR_UnableToStoreKey;
	public static String PasswordProvider_ERR_NoSuchAlgorithm;
	public static String PasswordProvider_ERR_NoSuchPadding;
	public static String PasswordProvider_ERR_InvalidKey;
	public static String PasswordProvider_ERR_IllegalBlockSize;
	public static String PasswordProvider_ERR_BadPadding;
	public static String PasswordProvider_ERR_UnableToDecodeExistingKey;
	public static String PasswordProvider_ERR_UnsupportedEncoding;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
