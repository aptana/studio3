package com.aptana.editor.common;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.messages"; //$NON-NLS-1$
	public static String AbstractThemeableEditor_CursorPositionLabel;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
