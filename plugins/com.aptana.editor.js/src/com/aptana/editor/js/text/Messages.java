package com.aptana.editor.js.text;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.js.text.messages"; //$NON-NLS-1$
	public static String JSTextHover_Example;
	public static String JSTextHover_NoDescription;
	public static String JSTextHover_openDeclarationTooltip;
	public static String JSTextHover_Specification;
	public static String JSTextHover_SupportedPlatforms;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
