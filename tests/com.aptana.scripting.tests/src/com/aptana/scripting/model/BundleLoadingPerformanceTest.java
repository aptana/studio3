package com.aptana.scripting.model;

import org.eclipse.test.performance.PerformanceTestCase;

public class BundleLoadingPerformanceTest extends PerformanceTestCase
{
	public void testLoadingUserBundles() throws Exception
	{
		System.setProperty("use.bundle.cache", Boolean.FALSE.toString());
		BundleManager manager = BundleManager.getInstance();
		
		for (int i = 0; i < 25; i++)
		{
			startMeasuring();
			manager.loadBundles();
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}
}
