package com.aptana.editor.html.contentassist.index;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.html.contentassist.index.messages"; //$NON-NLS-1$
	public static String HTMLFileIndexingParticipant_Error_During_Indexing;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
