package com.aptana.buildpath.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.aptana.buildpath.core.BuildPathEntryTest;
import com.aptana.core.build.CoreBuildTests;
import com.aptana.core.internal.build.InternalBuildTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CoreBuildTests.class, InternalBuildTests.class, BuildPathEntryTest.class })
public class BuildPathCoreTests
{

}
