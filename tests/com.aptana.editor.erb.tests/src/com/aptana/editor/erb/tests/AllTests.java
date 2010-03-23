package com.aptana.editor.erb.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.erb.RHTMLParserTest;
import com.aptana.editor.erb.RHTMLSourcePartitionScannerTest;
import com.aptana.editor.erb.html.RHTMLContentDescriberTest;
import com.aptana.editor.erb.xml.RXMLContentDescriberTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.erb"); //$NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(RHTMLSourcePartitionScannerTest.class);
		suite.addTestSuite(RHTMLParserTest.class);
		suite.addTestSuite(RHTMLContentDescriberTest.class);
		suite.addTestSuite(RXMLContentDescriberTest.class);
		// $JUnit-END$
		return suite;
	}
}
