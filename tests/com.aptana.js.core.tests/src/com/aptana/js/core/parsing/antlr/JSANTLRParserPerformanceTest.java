/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.antlr;

import com.aptana.js.core.parsing.JSParserPerformanceTest;
import com.aptana.parsing.IParser;

public class JSANTLRParserPerformanceTest extends JSParserPerformanceTest
{

	@Override
	protected IParser createParser()
	{
		return new JSWrappingParser();
	}
}