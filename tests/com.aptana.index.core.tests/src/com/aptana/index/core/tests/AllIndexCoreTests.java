package com.aptana.index.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.aptana.index.core.IndexCoreTests;
import com.aptana.index.core.build.BuildContextTest;
import com.aptana.internal.index.core.DiskIndexTest;

@RunWith(Suite.class)
@SuiteClasses({ DiskIndexTest.class, BuildContextTest.class, IndexCoreTests.class, })
public class AllIndexCoreTests
{

}
