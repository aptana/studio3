/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.randelshofer.quaqua.util.BinaryPListParserTest;

import com.aptana.core.internal.sourcemap.InternalSourcemapTests;
import com.aptana.core.util.AllUtilTests;
import com.aptana.plist.xml.XMLPListParserTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ IdeLogTest.class, BinaryPListParserTest.class, XMLPListParserTest.class, AllUtilTests.class,
		InternalSourcemapTests.class })
public class AllTests
{

}
