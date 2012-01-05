/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.internal.core.builtin;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.webserver.internal.core.builtin.messages"; //$NON-NLS-1$

	public static String LocalWebServer_ServerModeError;

	public static String LocalWebServerHttpRequestHandler_FILE_NOT_FOUND;
	public static String LocalWebServerHttpRequestHandler_FORBIDDEN;
	public static String LocalWebServerHttpRequestHandler_INTERNAL_SERVER_ERROR;
	public static String LocalWebServerHttpRequestHandler_UNSUPPORTED_METHOD;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
