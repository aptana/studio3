package com.aptana.editor.common.internal.theme;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.editor.common.internal.theme.messages"; //$NON-NLS-1$

	public static String ThemeManager_InvalidCharInThemeName;
	public static String ThemeManager_NameAlreadyExistsMsg;
	public static String ThemeManager_NameNonEmptyMsg;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
