/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.js.core.parsing.ast.messages"; //$NON-NLS-1$

	public static String JSBinaryArithmeticOperatorNode_0;
	public static String JSBinaryBooleanOperatorNode_0;
	public static String JSPostUnaryOperatorNode_0;
	public static String JSPreUnaryOperatorNode_0;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
