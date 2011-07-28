/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.ast;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Lindsey
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.parsing.ast.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	public static String ParseError_syntax_error_unexpected_token;
	public static String ParseNode_Bad_Ending_Offset;
	public static String ParseNodeAttribute_Undefined_Parent;
	public static String ParseNodeAttribute_Undefined_Name;
	public static String ParseNodeAttribute_Undefined_Value;
	public static String ParseNodeBase_Undefined_Child;
	public static String ParseNodeFactory_Undefined_Parse_State;
	public static String ParseNodeWalkerBase_Undefined_Node;
	public static String ParseNodeWalkerGroup_Undefined_Node_Processor;
}
