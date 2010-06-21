package com.aptana.editor.common.outline;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.common.outline.messages"; //$NON-NLS-1$

	public static String CommonOutlinePage_InitialFilterText;
	public static String CommonOutlinePage_Sorting_Description;
	public static String CommonOutlinePage_Sorting_LBL;
	public static String CommonOutlinePage_Sorting_TTP;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
