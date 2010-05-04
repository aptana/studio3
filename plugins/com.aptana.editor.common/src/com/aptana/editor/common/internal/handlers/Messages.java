package com.aptana.editor.common.internal.handlers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.editor.common.internal.handlers.messages"; //$NON-NLS-1$

	public static String ToggleOutlineHandler_ERR_OpeningOutline;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
