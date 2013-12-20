/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.epl.tests;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.core.epl.util.LRUCacheTest;
import com.aptana.core.epl.util.LRUCacheWithSoftPrunedValuesTest;
import com.aptana.core.epl.util.SoftHashMapTest;

@RunWith(Suite.class)
@SuiteClasses({LRUCacheTest.class, LRUCacheWithSoftPrunedValuesTest.class, SoftHashMapTest.class, })
public class AllTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite("Test for com.aptana.core.epl.tests");
//		// $JUnit-BEGIN$
//		suite.addTestSuite(LRUCacheTest.class);
//		suite.addTestSuite(LRUCacheWithSoftPrunedValuesTest.class);
//		suite.addTestSuite(SoftHashMapTest.class);
//		// $JUnit-END$
//		return suite;
//	}
}
