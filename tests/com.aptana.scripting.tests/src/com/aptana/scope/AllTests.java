package com.aptana.scope;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	ScopeSelectorTests.class,
	NameSelectorTests.class,
	AndSelectorTests.class,
	OrSelectorTests.class
})
public class AllTests
{

}
