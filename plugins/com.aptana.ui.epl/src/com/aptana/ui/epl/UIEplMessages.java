package com.aptana.ui.epl;

import org.eclipse.osgi.util.NLS;

public class UIEplMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.epl.UIEplMessages"; //$NON-NLS-1$
	public static String ExceptionHandler_seeErrorLogMessage;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, UIEplMessages.class);
	}

	private UIEplMessages()
	{
	}
}
