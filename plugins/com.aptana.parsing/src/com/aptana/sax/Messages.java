/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
