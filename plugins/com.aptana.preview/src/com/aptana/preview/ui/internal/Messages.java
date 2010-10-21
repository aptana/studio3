package com.aptana.preview.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.preview.ui.internal.messages"; //$NON-NLS-1$
	public static String SimpleWebServerPropertyDialog_BaseURL_Label;
	public static String SimpleWebServerPropertyDialog_DocRoot_Label;
	public static String SimpleWebServerPropertyDialog_DocumentRootError;
	public static String SimpleWebServerPropertyDialog_EmptyNameError;
	public static String SimpleWebServerPropertyDialog_InvalidURLError;
	public static String SimpleWebServerPropertyDialog_Name_Label;
	public static String SimpleWebServerPropertyDialog_ShellTitle;
	public static String SimpleWebServerPropertyDialog_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
