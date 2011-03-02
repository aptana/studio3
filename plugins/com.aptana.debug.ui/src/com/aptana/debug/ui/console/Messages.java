package com.aptana.debug.ui.console;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.debug.ui.console.messages"; //$NON-NLS-1$
	public static String ConsoleHyperlink_SourceNotFound_Message;
	public static String ConsoleHyperlink_SourceNotFound_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
