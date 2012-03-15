package com.aptana.editor.coffee.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.aptana.editor.coffee.CoffeeCodeScannerTest;
import com.aptana.editor.coffee.CoffeeDoubleClickStrategyTest;
import com.aptana.editor.coffee.CoffeeSourcePartitionScannerTest;
import com.aptana.editor.coffee.internal.build.CoffeeTaskDetectorTest;
import com.aptana.editor.coffee.internal.index.CoffeeFileIndexingParticipantTest;
import com.aptana.editor.coffee.internal.text.CoffeeFoldingComputerTest;
import com.aptana.editor.coffee.outline.CoffeeOutlineProviderTest;
import com.aptana.editor.coffee.parsing.CoffeeParserTest;
import com.aptana.editor.coffee.parsing.lexer.CoffeeScannerTest;
import com.aptana.editor.coffee.preferences.CoffeePreferencePageTest;

public class AllTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(CoffeeScannerTest.class);
		suite.addTestSuite(CoffeeParserTest.class);
		suite.addTestSuite(CoffeeFoldingComputerTest.class);
		suite.addTestSuite(CoffeeDoubleClickStrategyTest.class);
		suite.addTestSuite(CoffeeFileIndexingParticipantTest.class);
		suite.addTestSuite(CoffeeTaskDetectorTest.class);
		suite.addTestSuite(CoffeeCodeScannerTest.class);
		suite.addTestSuite(CoffeeOutlineProviderTest.class);
		suite.addTestSuite(CoffeeSourcePartitionScannerTest.class);
		suite.addTestSuite(CoffeescriptScopesTest.class);
		suite.addTestSuite(CoffeePreferencePageTest.class);
		// $JUnit-END$
		return suite;
	}

}
