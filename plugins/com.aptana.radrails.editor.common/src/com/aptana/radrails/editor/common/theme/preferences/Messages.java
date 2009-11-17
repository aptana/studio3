package com.aptana.radrails.editor.common.theme.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.radrails.editor.common.theme.preferences.messages"; //$NON-NLS-1$
	public static String ThemePreferencePage_BackgroundLabel;
	public static String ThemePreferencePage_ForegroundLabel;
	public static String ThemePreferencePage_LineHighlightLabel;
	public static String ThemePreferencePage_SelectionLabel;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
