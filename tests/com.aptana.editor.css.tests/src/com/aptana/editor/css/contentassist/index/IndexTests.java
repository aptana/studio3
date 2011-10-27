package com.aptana.editor.css.contentassist.index;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class IndexTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(IndexTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CSSFileIndexingParticipantTest.class);
		//$JUnit-END$
		return suite;
	}

}
