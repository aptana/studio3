package com.aptana.editor.js;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.js.messages"; //$NON-NLS-1$
	public static String Activator_Error_Loading_Metadata;
	public static String Loading_Metadata;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
