package com.aptana.terminal.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.terminal.editor.messages"; //$NON-NLS-1$
	public static String TerminalEditor_Part_Name;
	public static String TerminalEditor_Title_Tool_Tip;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
