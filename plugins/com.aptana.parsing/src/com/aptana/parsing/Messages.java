/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import org.eclipse.osgi.util.NLS;

/**
 * @author klindsey
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.parsing.messages"; //$NON-NLS-1$

	public static String ParserPoolFactory_Expecting_Source;
	public static String ParserPoolFactory_Cannot_Acquire_Parser;
	public static String ParserPoolFactory_Cannot_Acquire_Parser_Pool;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
