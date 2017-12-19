/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.tests;

import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceTestCase;

/**
 * All performance test cases extending this class will get tagged for participating in the global summary of elapsed
 * process time.
 * 
 * @author pinnamuri
 */
public class GlobalTimePerformanceTestCase extends PerformanceTestCase
{

	public GlobalTimePerformanceTestCase()
	{
	}

	public GlobalTimePerformanceTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
		super.setUp();
		tagAsGlobalSummary(Performance.getDefault().getDefaultScenarioId(this), Dimension.ELAPSED_PROCESS);
	}

}