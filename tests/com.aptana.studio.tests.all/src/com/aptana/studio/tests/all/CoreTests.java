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

import com.aptana.core.tests.StdErrLoggingSuite;

@RunWith(StdErrLoggingSuite.class)
//@formatter:off
@Suite.SuiteClasses({
	com.aptana.buildpath.core.tests.BuildPathCoreTests.class,
	com.aptana.core.tests.AllTests.class,
	com.aptana.core.io.tests.AllTests.class,
	// com.aptana.filesystem.ftp.tests.AllTests.class, // TODO Re-enable when FTP server is set back up?
	// com.aptana.filesystem.secureftp.tests.AllTests.class, // TODO Re-enable when FTP server is set back up?
	com.aptana.filesystem.http.tests.AllTests.class,
	com.aptana.git.core.tests.AllGitCoreTests.class,
	com.aptana.index.core.tests.AllIndexCoreTests.class,
	com.aptana.parsing.tests.AllTests.class,
	com.aptana.dtd.core.tests.AllTests.class,
	com.aptana.css.core.tests.AllTests.class,
	com.aptana.js.core.tests.AllJSCoreTests.class,
	com.aptana.xml.core.tests.AllTests.class,
	com.aptana.samples.tests.AllTests.class,
	com.aptana.scripting.tests.AllTests.class,
	com.aptana.jira.core.tests.AllJiraCoreTests.class,
	// com.aptana.syncing.core.tests.AllTests.class // TODO Re-enable when FTP server is set back up?
	com.aptana.usage.tests.AllTests.class,
})
// @formatter:on
public class CoreTests
{
}
