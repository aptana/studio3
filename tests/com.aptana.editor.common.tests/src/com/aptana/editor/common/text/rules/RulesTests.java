/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class RulesTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(RulesTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		//$JUnit-BEGIN$
		suite.addTestSuite(CaseInsensitiveMultiLineRuleTest.class);
		suite.addTestSuite(TagRuleTest.class);
		suite.addTestSuite(RegexpRuleTest.class);
		suite.addTestSuite(WhitespaceDetectorTest.class);
		suite.addTestSuite(WordDetectorTest.class);
		suite.addTestSuite(SingleCharacterRuleTest.class);
		suite.addTestSuite(SingleTagRuleTest.class);
		suite.addTestSuite(ExtendedWordRuleTest.class);
		//$JUnit-END$
		return suite;
	}

}
