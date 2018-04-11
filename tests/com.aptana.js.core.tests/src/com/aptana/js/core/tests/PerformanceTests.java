package com.aptana.js.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.js.core.parsing.JSFlexScannerPerformanceTest;
import com.aptana.js.core.parsing.JSParserPerformanceTest;
import com.aptana.js.internal.core.parsing.sdoc.SDocParserPerformanceTest;

@RunWith(Suite.class)
@SuiteClasses({ JSFlexScannerPerformanceTest.class, JSParserPerformanceTest.class, SDocParserPerformanceTest.class, })
public class PerformanceTests
{
}
