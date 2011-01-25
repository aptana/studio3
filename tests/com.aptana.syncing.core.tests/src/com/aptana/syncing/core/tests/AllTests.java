/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.syncing.core.tests;

import org.kohsuke.junit.ParallelTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new ParallelTestSuite(AllTests.class.getName(), 2);
		// $JUnit-BEGIN$
		suite.addTestSuite(LocalSyncingTests.class);
		suite.addTestSuite(LocalSyncingTestsWithSpaces.class);
		suite.addTestSuite(FTPSyncingTests.class);
		suite.addTestSuite(FTPSyncingTestsWithSpaces.class);
		suite.addTestSuite(SFTPSyncingTests.class);
		suite.addTestSuite(SFTPSyncingTestsWithSpaces.class);
		suite.addTestSuite(LocalLargeSampleSyncingTests.class);
		suite.addTestSuite(FTPLargeSampleSyncingTests.class);

		// $JUnit-END$
		return suite;
	}
}
