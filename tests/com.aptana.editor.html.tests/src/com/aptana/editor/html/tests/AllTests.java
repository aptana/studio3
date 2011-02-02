/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.html.HTMLEditorTest;
import com.aptana.editor.html.HTMLParserTest;
import com.aptana.editor.html.HTMLParserTypeAttributeTest;
import com.aptana.editor.html.HTMLScannerTest;
import com.aptana.editor.html.HTMLSourcePartitionScannerTest;
import com.aptana.editor.html.HTMLTagScannerTest;
import com.aptana.editor.html.HTMLOpenTagCloserTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.html.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(HTMLParserTest.class);
		suite.addTestSuite(HTMLParserTypeAttributeTest.class);
		suite.addTestSuite(HTMLScannerTest.class);
		suite.addTestSuite(HTMLSourcePartitionScannerTest.class);
		suite.addTestSuite(HTMLTagScannerTest.class);
//		suite.addTestSuite(HTMLTagScannerPerformanceTest.class);
		suite.addTestSuite(HTMLEditorTest.class);
		suite.addTestSuite(HTMLOpenTagCloserTest.class);
		suite.addTest(com.aptana.editor.html.outline.AllTests.suite());
		suite.addTest(com.aptana.editor.html.contentassist.AllTests.suite());
		// $JUnit-END$
		return suite;
	}
}
