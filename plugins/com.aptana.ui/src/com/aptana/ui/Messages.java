package com.aptana.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ui.messages"; //$NON-NLS-1$

	public static String DialogUtils_HideMessage;
	public static String UIPlugin_UpdateWarning_Message;
	public static String UIPlugin_UpdateWarning_Title;
	public static String UIUtils_Error;

    public static String IDialogConstants_LBL_Apply;
    public static String IDialogConstants_LBL_Browse;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
