package com.aptana.editor.erb.html;

import com.aptana.editor.erb.html.outline.RHTMLOutlineTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.erb.html"); //$NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(RHTMLContentDescriberTest.class);
		suite.addTestSuite(RHTMLEditorTest.class);
		suite.addTestSuite(RHTMLOutlineTest.class);
		// $JUnit-END$
		return suite;
	}
}
