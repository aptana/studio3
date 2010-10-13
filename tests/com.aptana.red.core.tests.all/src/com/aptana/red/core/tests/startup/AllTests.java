/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.red.core.tests.startup;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.tests.session.PerformanceSessionTestSuite;
import org.eclipse.core.tests.session.Setup;
import org.eclipse.core.tests.session.SetupManager.SetupException;
import org.eclipse.core.tests.session.UIPerformanceSessionTestSuite;

public class AllTests extends TestCase
{
	public static final String PLUGIN_ID = "com.aptana.red.core.tests.all";

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());

		// make sure that the first run of the startup test is not recorded - it is heavily
		// influenced by the presence and validity of the cached information
		try
		{
			PerformanceSessionTestSuite firstRun = new PerformanceSessionTestSuite(PLUGIN_ID, 1, StartupTest.class);
			Setup setup = firstRun.getSetup();
			setup.setSystemProperty("eclipseTest.ReportResults", "false");
			suite.addTest(firstRun);
		}
		catch (SetupException e)
		{
			fail("Unable to create warm up test");
		}

		// For this test to take advantage of the new runtime processing, we set "-eclipse.activateRuntimePlugins=false"
		try
		{
			PerformanceSessionTestSuite headlessSuite = new PerformanceSessionTestSuite(PLUGIN_ID, 5, StartupTest.class);
			Setup headlessSetup = headlessSuite.getSetup();
			headlessSetup.setSystemProperty("eclipse.activateRuntimePlugins", "false");
			suite.addTest(headlessSuite);
		}
		catch (SetupException e)
		{
			fail("Unable to setup headless startup performance test");
		}

		suite.addTest(new UIPerformanceSessionTestSuite(PLUGIN_ID, 5, UIStartupTest.class));
		suite.addTest(new UIPerformanceSessionTestSuite(PLUGIN_ID, 5, UIStartupWithJobsTest.class));
		return suite;
	}
}