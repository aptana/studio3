package com.aptana.ide.ui.io.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.ui.io.internal.messages"; //$NON-NLS-1$

	public static String FetchFileInfoJob_FailedToFetch;
	public static String FetchFileInfoJob_Title;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
