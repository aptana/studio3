/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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
