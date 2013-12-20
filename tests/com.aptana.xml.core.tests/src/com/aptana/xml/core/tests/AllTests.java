/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.aptana.xml.core.model.DTDTransformerTest;
import com.aptana.xml.core.parsing.XMLParserTest;

@RunWith(Suite.class)
//@formatter:off
@Suite.SuiteClasses({
	XMLParserTest.class,
	DTDTransformerTest.class
})
//@formatter:on
public class AllTests
{

}
