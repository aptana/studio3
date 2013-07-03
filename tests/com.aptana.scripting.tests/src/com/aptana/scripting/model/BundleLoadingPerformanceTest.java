package com.aptana.scripting.model;

import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.PerformanceTestCase;

public class BundleLoadingPerformanceTest extends PerformanceTestCase
{
	private BundleManager manager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		manager = BundleManager.getInstance();
		System.setProperty("use.bundle.cache", Boolean.FALSE.toString());
		manager.loadBundles();
	}

	@Override
	protected void tearDown() throws Exception
	{
		System.getProperties().remove("use.bundle.cache");
		super.tearDown();
	}

	public void testLoadingUserBundlesWithoutCache() throws Exception
	{
		tagAsGlobalSummary(getDefaultScenarioId(), Dimension.ELAPSED_PROCESS);
		System.setProperty("use.bundle.cache", Boolean.FALSE.toString());
		for (int i = 0; i < 25; i++)
		{
			startMeasuring();
			manager.loadBundles();
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

	public void testLoadingUserBundlesWithCache() throws Exception
	{
		tagAsGlobalSummary(getDefaultScenarioId(), Dimension.ELAPSED_PROCESS);
		System.setProperty("use.bundle.cache", Boolean.TRUE.toString());
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
