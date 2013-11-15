/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.git.ui.dialogs.CreateBranchDialogTest;
import com.aptana.git.ui.hyperlink.HyperlinkDetectorTest;
import com.aptana.git.ui.internal.DiffFormatterTest;
import com.aptana.git.ui.internal.GitLightweightDecoratorTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		// suite.addTestSuite(CreateBranchDialogTest.class);
		suite.addTestSuite(DiffFormatterTest.class);
		suite.addTestSuite(GitLightweightDecoratorTest.class);
		suite.addTestSuite(HyperlinkDetectorTest.class);
		// $JUnit-END$
		return suite;
	}

}
