/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.tests;

import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.editor.common.internal.peer.CharacterPairMatcherPerfTest;
import com.aptana.editor.common.internal.peer.PeerCharacterCloserPerfTest;
import com.aptana.editor.common.internal.scripting.DocumentScopeManagerPerformanceTest;
import com.aptana.editor.common.text.reconciler.RubyRegexpFolderPerformanceTest;
import com.aptana.editor.common.text.rules.ThemeingDamagerRepairerPerfTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(PerformanceTests.class.getName())
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
		suite.addTestSuite(CharacterPairMatcherPerfTest.class);
		suite.addTestSuite(DocumentScopeManagerPerformanceTest.class);
		suite.addTestSuite(PeerCharacterCloserPerfTest.class);
		suite.addTestSuite(RubyRegexpFolderPerformanceTest.class);
		suite.addTestSuite(ThemeingDamagerRepairerPerfTest.class);
		// $JUnit-END$
		return suite;
	}
}
