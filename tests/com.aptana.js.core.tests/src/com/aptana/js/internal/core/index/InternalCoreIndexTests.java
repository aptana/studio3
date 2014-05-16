package com.aptana.js.internal.core.index;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ JSIndexTest.class, JSMetadataIndexWriterTest.class, MetadataTest.class, JSCAParserTest.class })
public class InternalCoreIndexTests
{
}
