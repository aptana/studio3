package com.aptana.portal.ui.browser;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.portal.ui.browser.messages"; //$NON-NLS-1$
	public static String AbstractPortalBrowserEditor_ErrorMsg;
	public static String AbstractPortalBrowserEditor_ErrorTitle;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
