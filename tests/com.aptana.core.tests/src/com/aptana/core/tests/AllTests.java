/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.core.util.CollectionsUtilTest;
import com.aptana.core.util.EclipseUtilTest;
import com.aptana.core.util.FirefoxUtilTest;
import com.aptana.core.util.IOUtilTest;
import com.aptana.core.util.ResourceUtilTest;
import com.aptana.core.util.StringUtilTest;
import com.aptana.core.util.VersionUtilTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.core.util.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(CollectionsUtilTest.class);
		suite.addTestSuite(EclipseUtilTest.class);
		suite.addTestSuite(FirefoxUtilTest.class);
		suite.addTestSuite(IOUtilTest.class);
		suite.addTestSuite(ResourceUtilTest.class);
		suite.addTestSuite(StringUtilTest.class);
		suite.addTestSuite(VersionUtilTest.class);
		//$JUnit-END$
		return suite;
	}

}
