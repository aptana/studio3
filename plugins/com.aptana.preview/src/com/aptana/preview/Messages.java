package com.aptana.preview;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.preview.messages"; //$NON-NLS-1$
	public static String PreviewManager_UnsavedPrompt_Message;
	public static String PreviewManager_UnsavedPrompt_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
