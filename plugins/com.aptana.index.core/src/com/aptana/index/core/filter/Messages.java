package com.aptana.index.core.filter;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.index.core.filter.messages"; //$NON-NLS-1$
	public static String IndexFilterManager_Rebuilding_0;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
