package com.aptana.editor.erb.xml;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.erb.xml"); //$NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(RXMLContentDescriberTest.class);
		suite.addTestSuite(RXMLEditorTest.class);
		// $JUnit-END$
		return suite;
	}
}
