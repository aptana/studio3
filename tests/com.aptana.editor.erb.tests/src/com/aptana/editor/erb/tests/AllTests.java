package com.aptana.editor.erb.tests;

import com.aptana.editor.erb.RHTMLSourcePartitionScannerTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.erb"); //$NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(RHTMLSourcePartitionScannerTest.class);
		// $JUnit-END$
		return suite;
	}
}
