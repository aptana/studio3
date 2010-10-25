package com.aptana.editor.css.parsing;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.ide.editor.css.parsing");
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSCommentTest.class);
		suite.addTestSuite(CSSIdentifierTest.class);
		suite.addTestSuite(CSSKeywordTest.class);
		suite.addTestSuite(CSSLiteralTest.class);
		suite.addTestSuite(CSSPunctuatorTest.class);
		suite.addTestSuite(CSSParserTest.class);
		// $JUnit-END$
		return suite;
	}
}
