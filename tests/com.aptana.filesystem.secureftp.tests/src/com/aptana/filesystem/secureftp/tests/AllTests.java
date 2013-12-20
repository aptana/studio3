/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.secureftp.tests;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.kohsuke.junit.ParallelTestSuite;

import com.aptana.filesystem.secureftp.FTPSConnectionPointTest;
import com.aptana.filesystem.secureftp.SFTPConnectionPointTest;

@RunWith(Suite.class)
@SuiteClasses({SFTPConnectionPointTest.class, SFTPConnectionTest.class, FTPSConnectionPointTest.class, FTPSConnectionTest.class, FTPSConnectionWithBasePathTest.class, ImplicitFTPSConnectionTest.class, })
public class AllTests
{

//	public static Test suite()
//	{
//		TestSuite suite;
//		// Run in parallel locally, not on unit test build...
//		// String user = System.getenv("USER");
//		// if (user != null && user.equals("hudson"))
//		// {
//		// suite = new TestSuite(AllTests.class.getName());
//		// }
//		// else
//		// {
//		suite = new ParallelTestSuite(AllTests.class.getName(), 2);
//		// }
//		// $JUnit-BEGIN$
//		suite.addTestSuite(SFTPConnectionPointTest.class);
//		suite.addTestSuite(SFTPConnectionTest.class);
//		suite.addTestSuite(FTPSConnectionPointTest.class);
//		// suite.addTestSuite(FTPSConnectionTest.class);
//		// suite.addTestSuite(FTPSConnectionWithBasePathTest.class);
//		// suite.addTestSuite(ImplicitFTPSConnectionTest.class);
//		// $JUnit-END$
//		return suite;
//	}
}
