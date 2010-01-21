package com.aptana.editor.common.actions;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.actions.messages"; //$NON-NLS-1$

	public static String AbstractToggleLinkingAction_Description;
	public static String AbstractToggleLinkingAction_LBL;
	public static String AbstractToggleLinkingAction_TTP;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
