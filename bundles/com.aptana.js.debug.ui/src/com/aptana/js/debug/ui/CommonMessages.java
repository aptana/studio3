/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui;

import org.eclipse.osgi.util.NLS;

public class CommonMessages extends NLS {

	private static final String BUNDLE_NAME = "com.aptana.js.debug.ui.common_messages"; //$NON-NLS-1$

	public static String ProductName;
	public static String ProductShortName;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, CommonMessages.class);
	}

	private CommonMessages() {
	}
}
