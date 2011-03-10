package com.aptana.ui.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.ui.dialogs.messages"; //$NON-NLS-1$
	public static String InputURLDialog_InvalidURL;
	public static String TitaniumUpdatePopup_update_detail;
	public static String TitaniumUpdatePopup_update_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
