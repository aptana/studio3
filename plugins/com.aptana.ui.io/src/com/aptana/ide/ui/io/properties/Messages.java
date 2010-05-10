package com.aptana.ide.ui.io.properties;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.ide.ui.io.properties.messages"; //$NON-NLS-1$

	public static String FileInfoPropertyPage_Bytes;
	public static String FileInfoPropertyPage_ErrorStoreInfo;
	public static String FileInfoPropertyPage_FailedToFetchInfo;
	public static String FileInfoPropertyPage_File;
	public static String FileInfoPropertyPage_Folder;
	public static String FileInfoPropertyPage_Group;
	public static String FileInfoPropertyPage_Owner;
	public static String FileInfoPropertyPage_OwnerAndGroup;
	public static String FileInfoPropertyPage_Permissions;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
