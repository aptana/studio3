package com.aptana.editor.yaml.tests;

import com.aptana.editor.yaml.YAMLCodeScannerTest;
import com.aptana.editor.yaml.YAMLSourcePartitionScannerTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(YAMLSourcePartitionScannerTest.class);
		suite.addTestSuite(YAMLCodeScannerTest.class);
		//$JUnit-END$
		return suite;
	}

}
