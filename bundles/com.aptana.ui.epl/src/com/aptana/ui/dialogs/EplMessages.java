package com.aptana.ui.dialogs;

import org.eclipse.osgi.util.NLS;

public class EplMessages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.ui.dialogs.eplmessages"; //$NON-NLS-1$
	public static String ProjectContentsLocationArea_NonEmptyDirectory;
	public static String ProjectContentsLocationArea_OverlapError;
	public static String InputURLDialog_InvalidURL;
	public static String ProjectSelectionDialog_filter;
	public static String ProjectSelectionDialog_message;
	public static String ProjectSelectionDialog_title;
	public static String TitaniumUpdatePopup_update_detail;
	public static String TitaniumUpdatePopup_update_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, EplMessages.class);
	}

	private EplMessages() {
	}
}
