package com.aptana.editor.coffee.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.editor.coffee.CoffeeCodeScannerTest;
import com.aptana.editor.coffee.CoffeeDoubleClickStrategyTest;
import com.aptana.editor.coffee.CoffeeSourcePartitionScannerTest;
import com.aptana.editor.coffee.internal.build.CoffeeTaskDetectorTest;
import com.aptana.editor.coffee.internal.index.CoffeeFileIndexingParticipantTest;
import com.aptana.editor.coffee.internal.text.CoffeeFoldingComputerTest;
import com.aptana.editor.coffee.outline.CoffeeOutlineProviderTest;
import com.aptana.editor.coffee.parsing.CoffeeParserTest;
import com.aptana.editor.coffee.parsing.lexer.CoffeeScannerTest;

@RunWith(Suite.class)
@SuiteClasses({ CoffeeScannerTest.class, CoffeeParserTest.class, CoffeeFoldingComputerTest.class,
		CoffeeDoubleClickStrategyTest.class, CoffeeFileIndexingParticipantTest.class, CoffeeTaskDetectorTest.class,
		CoffeeCodeScannerTest.class, CoffeeOutlineProviderTest.class, CoffeeSourcePartitionScannerTest.class, })
public class AllTests
{
}
