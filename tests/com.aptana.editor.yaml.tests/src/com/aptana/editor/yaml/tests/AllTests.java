package com.aptana.editor.yaml.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.yaml.YAMLCodeScannerTest;
import com.aptana.editor.yaml.YAMLSourcePartitionScannerTest;
import com.aptana.editor.yaml.internal.text.YAMLFoldingComputerTest;
import com.aptana.editor.yaml.parsing.YAMLParserTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(YAMLSourcePartitionScannerTest.class);
		suite.addTestSuite(YAMLCodeScannerTest.class);
		suite.addTestSuite(YAMLFoldingComputerTest.class);
		suite.addTestSuite(YAMLParserTest.class);
		// $JUnit-END$
		return suite;
	}

}
