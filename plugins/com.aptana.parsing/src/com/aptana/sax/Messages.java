/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.sax;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Lindsey
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.sax.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	public static String Schema_Element_Stack_Trace;
	public static String Schema_Missing_Root_Element;
	public static String SchemaBuilder_Insufficient_Reflection_Security;
	public static String SchemaBuilder_Missing_Handler_Method;
	public static String SchemaBuilder_File_Unlocatable;
	public static String SchemaBuilder_SAX_Parser_Initialization_Error;
	public static String SchemaBuilder_SAX_Parser_Error;
	public static String SchemaBuilder_IO_Error;
	public static String SchemaElement_Attribute_already_defined;
	public static String SchemaElement_Invalid_attribute_on_tag;
	public static String SchemaElement_Not_valid_usage_attribute;
	public static String SchemaElement_Undefined_Owning_Schema;
	public static String SchemaElement_Undefined_Name;
	public static String SchemaElement_Undefined_Node;
	public static String Schema_Invalid_Child;
	public static String SchemaBuilder_Unknown_Schema_Namespace;
	public static String SchemaBuilder_Unable_To_Get_OnEnter_Method;
	public static String SchemaBuilder_Unable_To_Locate_OnEnter_Method;
	public static String SchemaBuilder_Unable_To_Get_OnExit_Method;
	public static String SchemaBuilder_Unable_To_Locate_OnExit_Method;
	public static String SchemaBuilder_Set_ID_Not_Defined;
}
