package com.aptana.webserver.core.builtin;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.webserver.core.builtin.messages"; //$NON-NLS-1$
	public static String LocalWebServerHttpRequestHandler_FILE_NOT_FOUND;
	public static String LocalWebServerHttpRequestHandler_FORBIDDEN;
	public static String LocalWebServerHttpRequestHandler_INTERNAL_SERVER_ERROR;
	public static String LocalWebServerHttpRequestHandler_UNSUPPORTED_METHOD;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
