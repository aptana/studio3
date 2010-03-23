package com.aptana.git.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.git.ui.internal.messages"; //$NON-NLS-1$
	public static String QuickDiffReferenceProvider_Error_NotEnoughBytesForBOM;
	public static String QuickDiffReferenceProvider_Error_WrongByteOrderMark;
	public static String QuickDiffReferenceProvider_ReadJob_label;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
