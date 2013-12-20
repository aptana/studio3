/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.aptana.scripting.BundleConverterTest;

@RunWith(Suite.class)
//@formatter:off
@Suite.SuiteClasses({
	com.aptana.scripting.model.AllTests.class,
	com.aptana.scope.AllTests.class,
	com.aptana.scope.parsing.AllScopeParsingTests.class,
	BundleConverterTest.class,
	UnicodeCharsJRubyTest.class
})
//@formatter:on
public class AllTests
{

}
