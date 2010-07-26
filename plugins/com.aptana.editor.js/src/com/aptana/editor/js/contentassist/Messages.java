package com.aptana.editor.js.contentassist;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.js.contentassist.messages"; //$NON-NLS-1$
	public static String JSModelFormatter_Defined_Section_Header;
	public static String JSModelFormatter_Exampes_Section_Header;
	public static String JSModelFormatter_Specification_Header;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
