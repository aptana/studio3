package com.aptana.editor.common.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.preferences.messages"; //$NON-NLS-1$
	public static String UserAgentPreferencePage_Select_All;
	public static String UserAgentPreferencePage_Select_None;
	public static String UserAgentPreferencePage_Select_User_Agents;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
