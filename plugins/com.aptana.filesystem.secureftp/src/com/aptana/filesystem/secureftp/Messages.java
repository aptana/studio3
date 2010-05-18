package com.aptana.filesystem.secureftp;

import org.eclipse.osgi.util.NLS;

/* package */ class Messages extends NLS {

	private static final String BUNDLE_NAME = "com.aptana.filesystem.secureftp.messages"; //$NON-NLS-1$

	public static String SecureUtils_InvalidPrivateKey;
	public static String SecureUtils_UnableToReadPrivateKey;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
