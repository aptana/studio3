/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.security.internal.linux;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$
	
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
