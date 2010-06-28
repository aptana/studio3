package com.aptana.editor.html.contentassist;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.html.contentassist.messages"; //$NON-NLS-1$
	public static String HTMLIndexQueryHelper_Error_Loading_Metadata;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
