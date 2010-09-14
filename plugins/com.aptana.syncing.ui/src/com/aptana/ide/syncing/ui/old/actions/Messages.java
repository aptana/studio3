package com.aptana.ide.syncing.ui.old.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.syncing.ui.old.actions.messages"; //$NON-NLS-1$

	public static String SiteConnectionSynchronizeAction_UnableToOpenSyncDialog;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
