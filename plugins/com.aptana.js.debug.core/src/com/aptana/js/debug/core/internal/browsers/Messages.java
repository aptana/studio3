/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.browsers;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.js.debug.core.internal.browsers.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String BrowserUtil_FirefoxProfileNotFound;

	/**
	 * BrowserUtil_InstallingDebugExtension
	 */
	public static String BrowserUtil_InstallingDebugExtension;

	/**
	 * BrowserUtil_InstallError
	 */
	public static String BrowserUtil_InstallError;

	public static String BrowserUtil_PreviousVersionFound_Message;

}
