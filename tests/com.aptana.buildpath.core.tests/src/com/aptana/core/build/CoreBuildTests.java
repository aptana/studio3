package com.aptana.core.build;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AbstractBuildParticipantTest.class, ReconcileContextTest.class,
		RequiredBuildParticipantTest.class, UnifiedBuilderTest.class })
public class CoreBuildTests
{

}
