/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
// @formatter:off
@SuiteClasses({
	BundleCacherTest.class,
	BundleTests.class,
	BundleVisibilityTests.class,
	CommandBlockRunnerTests.class,
	CommandTests.class,
	ContextTests.class,
	CommandBlockRunnerTests.class,
	FilterTests.class,
	KeyBindingTests.class,
	PlatformSpecificCommandTests.class,
	SnippetCategoryTests.class,
	WithDefaultsTests.class,
	ProjectBundleMonitorTests.class,
	// TODO: uncomment once timing issues are resolved. We're still getting intermittent failures
	// UserBundleMonitorTests.class,
})
// @formatter:on
public class AllTests
{
}
