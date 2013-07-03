/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.studio.tests.all;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class CoreTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(CoreTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.buildpath.core.tests.BuildPathCoreTests.suite());
		suite.addTest(com.aptana.core.tests.AllTests.suite());
		suite.addTest(com.aptana.core.io.tests.AllTests.suite());
		// suite.addTest(com.aptana.filesystem.ftp.tests.AllTests.suite());
		// suite.addTest(com.aptana.filesystem.secureftp.tests.AllTests.suite());
		suite.addTest(com.aptana.filesystem.http.tests.AllTests.suite());
		suite.addTest(com.aptana.git.core.tests.AllGitCoreTests.suite());
		suite.addTest(com.aptana.index.core.tests.AllIndexCoreTests.suite());
		suite.addTest(com.aptana.parsing.tests.AllTests.suite());
		suite.addTest(com.aptana.dtd.core.tests.AllTests.suite());
		suite.addTest(com.aptana.css.core.tests.AllTests.suite());
		suite.addTest(com.aptana.js.core.tests.AllJSCoreTests.suite());
		suite.addTest(com.aptana.xml.core.tests.AllTests.suite());
		suite.addTest(com.aptana.samples.tests.AllTests.suite());
		suite.addTest(com.aptana.scripting.tests.AllTests.suite());
		suite.addTest(com.aptana.jira.core.tests.AllJiraCoreTests.suite());
		// suite.addTest(com.aptana.syncing.core.tests.AllTests.suite());
		// $JUnit-END$
		return suite;
	}
}
