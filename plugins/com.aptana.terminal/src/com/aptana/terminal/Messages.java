package com.aptana.terminal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.terminal.messages"; //$NON-NLS-1$
	public static String TerminalBrowser_Copy;
	public static String TerminalBrowser_Copy_From_Terminal;
	public static String TerminalBrowser_Copy_Selected_Text;
	public static String TerminalBrowser_Key_Binding_Scheme_Does_Not_Exist;
	public static String TerminalBrowser_Paste;
	public static String TerminalBrowser_Paste_Clipboard_Text;
	public static String TerminalBrowser_Paste_Into_Terminal;
	public static String TerminalBrowser_Select_All;
	public static String TerminalBrowser_Select_All_In_Terminal;
	public static String TerminalBrowser_Unable_To_Restore_Key_Binding;
	public static String Utils_Unable_To_Open_Editor;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
