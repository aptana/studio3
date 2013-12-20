/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.studio.tests.startup;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.tests.session.PerformanceSessionTestSuite;
import org.eclipse.core.tests.session.Setup;
import org.eclipse.core.tests.session.SetupManager.SetupException;
import org.eclipse.core.tests.session.UIPerformanceSessionTestSuite;

@RunWith(Suite.class)
@SuiteClasses({})
public class AllTests
{
	public static final String PLUGIN_ID = "com.aptana.studio.tests.all";

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite("Startup Performance Tests");
//
//		// make sure that the first run of the startup test is not recorded - it is heavily
//		// influenced by the presence and validity of the cached information
//		try
//		{
//			PerformanceSessionTestSuite firstRun = new PerformanceSessionTestSuite(PLUGIN_ID, 1, StartupTest.class);
//			Setup setup = firstRun.getSetup();
//			setup.setSystemProperty("eclipseTest.ReportResults", "false");
//			suite.addTest(firstRun);
//		}
//		catch (SetupException e)
//		{
//			fail("Unable to create warm up test");
//		}
//
//		// For this test to take advantage of the new runtime processing, we set "-eclipse.activateRuntimePlugins=false"
//		try
//		{
//			PerformanceSessionTestSuite headlessSuite = new PerformanceSessionTestSuite(PLUGIN_ID, 5, StartupTest.class);
//			Setup headlessSetup = headlessSuite.getSetup();
//			headlessSetup.setSystemProperty("eclipse.activateRuntimePlugins", "false");
//			suite.addTest(headlessSuite);
//		}
//		catch (SetupException e)
//		{
//			fail("Unable to setup headless startup performance test");
//		}
//
//		suite.addTest(new UIPerformanceSessionTestSuite(PLUGIN_ID, 5, UIStartupTest.class));
//		suite.addTest(new UIPerformanceSessionTestSuite(PLUGIN_ID, 5, UIStartupWithJobsTest.class));
//		return suite;
//	}
}
