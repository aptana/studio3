/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.studio.tests.all;

import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All performance tests")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				String msg = MessageFormat.format("Running test: {0}", test.toString());
				System.err.println(msg);
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.studio.tests.startup.AllTests.suite());
		suite.addTest(com.aptana.git.core.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.js.core.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.scripting.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.editor.common.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.xml.core.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.editor.css.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.editor.js.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.editor.html.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.editor.json.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.theme.tests.PerformanceTests.suite());
		// $JUnit-END$
		return suite;
	}
}
