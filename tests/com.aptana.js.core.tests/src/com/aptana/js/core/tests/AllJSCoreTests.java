/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.js.core.build.CoreBuildTests;
import com.aptana.js.core.index.JSIndexQueryHelperTest;
import com.aptana.js.core.inferencing.CoreInferencingTests;
import com.aptana.js.core.model.ReturnTypeElementTest;
import com.aptana.js.core.parsing.CoreParsingTests;
import com.aptana.js.internal.core.build.InternalCoreBuildTests;
import com.aptana.js.internal.core.index.InternalCoreIndexTests;
import com.aptana.js.internal.core.inferencing.InternalCoreInferencingTests;
import com.aptana.js.internal.core.parsing.InternalCoreParsingTests;
import com.aptana.js.internal.core.parsing.sdoc.InternalCoreParsingSDocTests;

public class AllJSCoreTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllJSCoreTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTest(CoreBuildTests.suite());
		suite.addTestSuite(JSIndexQueryHelperTest.class);
		suite.addTest(CoreInferencingTests.suite());
		suite.addTest(CoreParsingTests.suite());
		suite.addTest(InternalCoreBuildTests.suite());
		suite.addTest(InternalCoreIndexTests.suite());
		suite.addTest(InternalCoreInferencingTests.suite());
		suite.addTest(InternalCoreParsingTests.suite());
		suite.addTest(InternalCoreParsingSDocTests.suite());
		suite.addTestSuite(ReturnTypeElementTest.class);
		// $JUnit-END$
		return suite;
	}

}
