package com.aptana.git.ui.internal.dialogs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.dialogs.messages"; //$NON-NLS-1$
	public static String BranchDialog_msg;
	public static String BranchDialog_title;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
