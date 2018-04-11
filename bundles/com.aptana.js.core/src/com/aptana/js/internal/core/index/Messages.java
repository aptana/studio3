/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import org.eclipse.osgi.util.NLS;

/**
 * @author Robin
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.js.internal.core.index.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String JSMetadataLoader_Rebuilding_Project_Indexes;

	public static String Loading_Metadata;

	public static String JSMetadataReader_Invalid_Base_Type;
	public static String JSMetadataReader_Invalid_Exception_Name;
	public static String JSMetadataReader_Invalid_Parameter_Type;
	public static String JSMetadataReader_Invalid_Property_Type;
	public static String JSMetadataReader_Invalid_Return_Type;
	public static String JSMetadataReader_Invalid_Type_Name;
}
