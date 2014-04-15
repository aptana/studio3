/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.studio.tests.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
// @formatter:off
@Suite.SuiteClasses({
	com.aptana.studio.tests.startup.AllTests.class,
	com.aptana.git.core.tests.PerformanceTests.class,
	com.aptana.js.core.tests.PerformanceTests.class,
	com.aptana.scripting.tests.PerformanceTests.class,
	com.aptana.editor.common.tests.PerformanceTests.class,
	com.aptana.xml.core.tests.PerformanceTests.class,
	com.aptana.editor.css.tests.PerformanceTests.class,
	com.aptana.editor.js.tests.PerformanceTests.class,
	com.aptana.editor.html.tests.PerformanceTests.class,
	com.aptana.editor.json.tests.PerformanceTests.class,
	com.aptana.theme.tests.PerformanceTests.class })
// @formatter:on
public class PerformanceTests
{
}
