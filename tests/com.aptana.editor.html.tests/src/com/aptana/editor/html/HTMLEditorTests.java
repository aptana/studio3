/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.aptana.editor.html.validator.HTMLValidatorTests;

public class HTMLEditorTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(HTMLEditorTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(HTMLEditorTest.class);
		suite.addTestSuite(HTMLFoldingComputerTest.class);
		suite.addTestSuite(HTMLOpenTagCloserTest.class);
		suite.addTestSuite(HTMLParserTest.class);
		suite.addTestSuite(HTMLParserTypeAttributeTest.class);
		suite.addTestSuite(HTMLScannerTest.class);
		suite.addTestSuite(HTMLSourcePartitionScannerTest.class);
		// suite.addTestSuite(HTMLTagScannerPerformanceTest.class);
		suite.addTestSuite(HTMLTagScannerTest.class);
		suite.addTestSuite(HTMLTagUtilTest.class);
		suite.addTestSuite(HTMLValidatorTests.class);
		// $JUnit-END$
		return suite;
	}

}
