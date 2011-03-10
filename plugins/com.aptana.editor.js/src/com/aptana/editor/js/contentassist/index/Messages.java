/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

import org.eclipse.osgi.util.NLS;

/**
 * @author Robin
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.js.contentassist.index.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String JSCAHandler_Invalid_Alias;
	public static String JSCAHandler_Invalid_Event_Name;
	public static String JSCAHandler_Invalid_Event_Property_Name;
	public static String JSCAHandler_Invalid_Function_Name;
	public static String JSCAHandler_Invalid_Parameter_Name;
	public static String JSCAHandler_Invalid_Property_Name;
	public static String JSCAHandler_Invalid_Type_Name;
	public static String JSCAHandler_Unable_To_Set_Name_Property;
	public static String JSCAHandler_Unrecognized_Property_Name;
	public static String JSMetadataReader_Invalid_Exception_Name;
	public static String JSMetadataReader_Invalid_Parameter_Type;
	public static String JSMetadataReader_Invalid_Property_Type;
	public static String JSMetadataReader_Invalid_Return_Type;
	public static String JSMetadataReader_Invalid_Supertype_Name;
	public static String JSMetadataReader_Invalid_Type_Name;
}
