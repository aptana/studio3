/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.editor.xml.OpenTagCloserTest;
import com.aptana.editor.xml.TagUtilTest;
import com.aptana.editor.xml.XMLEditorTest;
import com.aptana.editor.xml.XMLPartitionScannerTest;
import com.aptana.editor.xml.XMLScannerTest;
import com.aptana.editor.xml.XMLTagScannerTest;
import com.aptana.editor.xml.contentassist.XMLContentAssistProcessorTest;
import com.aptana.editor.xml.internal.text.XMLFoldingComputerTest;
import com.aptana.editor.xml.outline.XMLOutlineTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(TagUtilTest.class);
		suite.addTestSuite(XMLContentAssistProcessorTest.class);
		suite.addTestSuite(XMLPartitionScannerTest.class);
		suite.addTestSuite(XMLScannerTest.class);
		suite.addTestSuite(XMLFoldingComputerTest.class);
		suite.addTestSuite(XMLTagScannerTest.class);
		suite.addTestSuite(XMLEditorTest.class);
		suite.addTestSuite(XMLOutlineTest.class);
		suite.addTestSuite(OpenTagCloserTest.class);
		// $JUnit-END$
		return suite;
	}

}
