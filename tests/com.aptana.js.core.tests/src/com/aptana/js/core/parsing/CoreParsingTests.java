package com.aptana.js.core.parsing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ JSFlexScannerTest.class, GraalJSParserTest.class, SDocNodeAttachmentTest.class, })
public class CoreParsingTests
{
}
