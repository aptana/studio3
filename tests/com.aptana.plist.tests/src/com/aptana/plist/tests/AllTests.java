package com.aptana.plist.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import ch.randelshofer.quaqua.util.BinaryPListParserTest;

import com.aptana.plist.xml.XMLPListParserTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(BinaryPListParserTest.class);
		suite.addTestSuite(XMLPListParserTest.class);
		// $JUnit-END$
		return suite;
	}

}
