package com.aptana.scripting.keybindings.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.scripting.keybindings.internal.messages"; //$NON-NLS-1$
	public static String KeybindingsManager_AptanaProxyCommand;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
