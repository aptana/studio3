/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.git.core.GitCoreTests;
import com.aptana.git.core.model.CoreModelTests;
import com.aptana.git.internal.core.storage.CoreStorageTests;

public class AllGitCoreTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllGitCoreTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTest(CoreModelTests.suite());
		suite.addTest(CoreStorageTests.suite());
		suite.addTest(GitCoreTests.suite());
		// $JUnit-END$
		return suite;
	}

}
