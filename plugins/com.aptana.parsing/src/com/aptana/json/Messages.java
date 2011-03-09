/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import org.eclipse.osgi.util.NLS;

/**
 * @author klindsey
 *
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.json.messages"; //$NON-NLS-1$
	public static String Schema_Expected_End_Of_Array;
	public static String Schema_Expected_End_Of_Object;
	public static String Schema_Expected_Primitive;
	public static String Schema_Expected_Start_Of_Array;
	public static String Schema_Expected_Start_Of_Object;
	public static String Schema_Unsupported_Event;
	public static String SchemaArray_Cannot_End_Unstarted_Array;
	public static String SchemaArray_Cannot_End_Unstarted_Array_Element;
	public static String SchemaArray_Cannot_Start_Started_Array;
	public static String SchemaArray_Cannot_Start_Started_Array_Element;
	public static String SchemaArray_Unsupported_Event;
	public static String SchemaContext_Popped_Empty_Stack;
	public static String SchemaObject_Cannot_End_Unstarted_Object;
	public static String SchemaObject_Cannot_End_Unstarted_Object_Entry;
	public static String SchemaObject_Cannot_Start_Started_Object;
	public static String SchemaObject_Cannot_Start_Started_Object_Entry;
	public static String SchemaObject_Nonexistant_Property;
	public static String SchemaObject_Property_Must_Have_Name;
	public static String SchemaObject_Unsupported_Event;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
