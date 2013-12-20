/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.aptana.parsing.ParseStateCacheKeyWithCommentsTest;

@RunWith(Suite.class)
// @formatter:off
@Suite.SuiteClasses({
	ParseStateCacheKeyWithCommentsTest.class,
	ParseStateTest.class,
	com.aptana.json.AllTests.class,
	com.aptana.parsing.ast.AllTests.class,
	com.aptana.parsing.lexer.LexerTests.class,
	com.aptana.parsing.pool.AllTests.class,
	com.aptana.sax.AllTests.class
})
// @formatter:on
public class AllTests
{

}
