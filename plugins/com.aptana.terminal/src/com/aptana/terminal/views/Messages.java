package com.aptana.terminal.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.terminal.views.messages"; //$NON-NLS-1$
	public static String TerminalView_Create_Terminal_Editor_Tooltip;
	public static String TerminalView_Open_Terminal_Editor;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
