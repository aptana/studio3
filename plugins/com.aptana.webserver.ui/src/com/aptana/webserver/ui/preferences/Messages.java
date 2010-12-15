package com.aptana.webserver.ui.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.webserver.ui.preferences.messages"; //$NON-NLS-1$
	public static String LocalWebServerPreferencePage_Address_Label;
	public static String LocalWebServerPreferencePage_Message;
	public static String LocalWebServerPreferencePage_Port_Label;
	public static String LocalWebServerPreferencePage_PortError_Message;
	public static String ServersPreferencePage_DeletePrompt_Message;
	public static String ServersPreferencePage_DeletePrompt_Title;
	public static String ServersPreferencePage_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
