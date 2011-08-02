package com.aptana.editor.html;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class HTMLEditorTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(HTMLEditorTests.class.getName());
		//$JUnit-BEGIN$
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
		//$JUnit-END$
		return suite;
	}

}
