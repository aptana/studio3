package com.aptana.explorer.internal.handlers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.explorer.internal.handlers.messages"; //$NON-NLS-1$

	public static String DeployHandler_Wizard_Title;

	public static String ToggleAppExplorerHandler_ERR_OpeningAppExplorer;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
