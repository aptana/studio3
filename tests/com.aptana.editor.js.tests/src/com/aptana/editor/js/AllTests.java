package com.aptana.editor.js;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.js");
		// $JUnit-BEGIN$
		suite.addTestSuite(JSParserTest.class);
		// suite.addTestSuite(JSCodeScannerPerformanceTest.class);
		suite.addTestSuite(JSCodeScannerTest.class);
		suite.addTestSuite(JSSourcePartitionScannerTest.class);
		// suite.addTestSuite(JSParserPerformanceTest.class);
		suite.addTestSuite(JSSingleQuotedStringScannerTest.class);
		suite.addTestSuite(JSDoubleQuotedStringScannerTest.class);
		suite.addTestSuite(JSRegexScannerTest.class);
		suite.addTestSuite(JSDocScannerTest.class);
		suite.addTestSuite(JSEditorTest.class);
		suite.addTestSuite(SDocTokenScannerTests.class);
		suite.addTestSuite(SDocTypeTokenScannerTests.class);
		suite.addTestSuite(SDocScannerTests.class);
		// $JUnit-END$
		return suite;
	}

}
