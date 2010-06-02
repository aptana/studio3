package com.aptana.terminal.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.terminal.internal.messages"; //$NON-NLS-1$
	public static String TerminalCloseHelper_DialogMessage;
	public static String TerminalCloseHelper_DialogTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
