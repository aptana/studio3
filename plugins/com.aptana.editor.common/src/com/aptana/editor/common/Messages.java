package com.aptana.editor.common;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.messages"; //$NON-NLS-1$
	private static final String BUNDLE_FOR_CONSTRUCTED_KEYS = BUNDLE_NAME;
	private static ResourceBundle fgBundleForConstructedKeys = ResourceBundle.getBundle(BUNDLE_FOR_CONSTRUCTED_KEYS);
	
	public static String AbstractThemeableEditor_CursorPositionLabel;
	public static String FileService_FailedToParse;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}

	public static ResourceBundle getBundleForConstructedKeys()
	{
		return fgBundleForConstructedKeys;
	}
}
