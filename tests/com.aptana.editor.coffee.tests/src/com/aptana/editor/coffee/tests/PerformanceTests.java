package com.aptana.editor.coffee.tests;

import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.coffee.CoffeeScriptEditorPlugin;
import com.aptana.editor.coffee.parsing.lexer.CoffeeScannerPerformanceTest;

public class PerformanceTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(PerformanceTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				String msg = MessageFormat.format("Running test: {0}", test.toString());
				IdeLog.logError(CoffeeScriptEditorPlugin.getDefault(), msg);
				System.out.println(msg);
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(CoffeeScannerPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}

}
