package com.aptana.editor.js.contentassist;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.js.contentassist.messages"; //$NON-NLS-1$
	public static String JSContentAssistProcessor_Referencing_Files;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
