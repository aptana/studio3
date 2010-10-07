package com.aptana.red.core.tests.startup;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;

public class UIStartupTest extends TestCase
{

	public static Test suite()
	{
		return new TestSuite(UIStartupTest.class);
	}

	public UIStartupTest(String methodName)
	{
		super(methodName);
	}

	public void testUIApplicationStartup()
	{
		PerformanceMeter meter = Performance.getDefault()
				.createPerformanceMeter(getClass().getName() + '.' + getName());
		try
		{
			meter.stop();
			Performance.getDefault().tagAsGlobalSummary(meter, "Core UI Startup", Dimension.ELAPSED_PROCESS);
			meter.commit();
			Performance.getDefault().assertPerformanceInRelativeBand(meter, Dimension.ELAPSED_PROCESS, -50, 5);
		}
		finally
		{
			meter.dispose();
		}
	}
}