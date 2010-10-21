package com.aptana.preview.ui.properties;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.preview.ui.properties.messages"; //$NON-NLS-1$
	public static String ProjectPreviewPropertyPage_Server_Label;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
