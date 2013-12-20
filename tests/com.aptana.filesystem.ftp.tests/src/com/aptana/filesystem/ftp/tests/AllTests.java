/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.ftp.tests;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.kohsuke.junit.ParallelTestSuite;

import com.aptana.filesystem.ftp.FTPConnectionPointTest;

@RunWith(Suite.class)
@SuiteClasses({FTPConnectionPointTest.class, FTPConnectionTest.class, FTPConnectionWithBasePathTest.class, })
public class AllTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new ParallelTestSuite(AllTests.class.getName(), 2);
//		// $JUnit-BEGIN$
//		suite.addTestSuite(FTPConnectionPointTest.class);
//		suite.addTestSuite(FTPConnectionTest.class);
//		suite.addTestSuite(FTPConnectionWithBasePathTest.class);
//		// $JUnit-END$
//		return suite;
//	}
}
