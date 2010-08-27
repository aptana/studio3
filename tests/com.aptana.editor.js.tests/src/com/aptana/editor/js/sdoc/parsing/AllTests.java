package com.aptana.editor.js.sdoc.parsing;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.js.sdoc.parsing");
		//$JUnit-BEGIN$
		suite.addTestSuite(SDocParserTests.class);
		suite.addTestSuite(SDocTokenScannerTests.class);
		suite.addTestSuite(SDocTypeTokenScannerTests.class);
		suite.addTestSuite(SDocScannerTests.class);
		//$JUnit-END$
		return suite;
	}

}
