package com.aptana.scripting.model;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.scripting.model.messages"; //$NON-NLS-1$
	public static String BundleManager_Cannot_Locate_Built_Ins_Directory;
	public static String BundleManager_Malformed_Built_Ins_URI;
	public static String BundleManager_Missing_Bundle_File;
	public static String BundleManager_NO_BUNDLES;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
