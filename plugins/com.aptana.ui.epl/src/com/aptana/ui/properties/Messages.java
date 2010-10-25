package com.aptana.ui.properties;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ui.properties.messages"; //$NON-NLS-1$

	public static String ProjectNaturesPage_CloseProjectJob_Title;
	public static String ProjectNaturesPage_Description;
	public static String ProjectNaturesPage_ERR_CloseProject;
	public static String ProjectNaturesPage_ERR_OpenProject;
	public static String ProjectNaturesPage_ERR_RetrieveNatures;
	public static String ProjectNaturesPage_ERR_SetNatures;
	public static String ProjectNaturesPage_LBL_MakePrimary;
	public static String ProjectNaturesPage_LBL_Primary;
	public static String ProjectNaturesPage_LBL_SetAsPrimary;
	public static String ProjectNaturesPage_ResetMessage;
	public static String ProjectNaturesPage_ResetTitle;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
