package com.aptana.editor.xml.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.xml.XMLEditorTest;
import com.aptana.editor.xml.XMLParserTest;
import com.aptana.editor.xml.XMLPartitionScannerTest;
import com.aptana.editor.xml.XMLScannerTest;
import com.aptana.editor.xml.XMLTagScannerTest;
import com.aptana.editor.xml.outline.XMLOutlineTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(XMLPartitionScannerTest.class);
		suite.addTestSuite(XMLScannerTest.class);
		suite.addTestSuite(XMLTagScannerTest.class);
		suite.addTestSuite(XMLParserTest.class);
		suite.addTestSuite(XMLEditorTest.class);
		suite.addTestSuite(XMLOutlineTest.class);
		// $JUnit-END$
		return suite;
	}

}
