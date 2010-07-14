package com.aptana.formatter.epl;

import org.eclipse.osgi.util.NLS;

public class FormatterMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.formatter.epl.FormatterMessages"; //$NON-NLS-1$
	public static String ExceptionHandler_seeErrorLogMessage;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, FormatterMessages.class);
	}

	private FormatterMessages()
	{
	}
}
