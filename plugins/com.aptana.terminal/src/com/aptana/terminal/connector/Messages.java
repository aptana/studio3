package com.aptana.terminal.connector;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.terminal.connector.messages"; //$NON-NLS-1$
	public static String LocalTerminalConnector_NoShellErrorMessage;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
