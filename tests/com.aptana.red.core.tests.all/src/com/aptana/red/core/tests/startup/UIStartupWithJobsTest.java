package com.aptana.red.core.tests.startup;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.test.performance.Dimension;
import org.eclipse.test.performance.Performance;
import org.eclipse.test.performance.PerformanceMeter;

import com.aptana.editor.epl.tests.EditorTestHelper;

public class UIStartupWithJobsTest extends TestCase
{

	public static Test suite()
	{
		return new TestSuite(UIStartupWithJobsTest.class);
	}

	public UIStartupWithJobsTest(String methodName)
	{
		super(methodName);
	}

	public void testUIApplicationStartup() throws Exception
	{
		PerformanceMeter meter = Performance.getDefault()
				.createPerformanceMeter(getClass().getName() + '.' + getName());
		try
		{
			EditorTestHelper.showPerspective(EditorTestHelper.WEB_PERSPECTIVE_ID);
			EditorTestHelper.joinBackgroundActivities();
			meter.stop();
			Performance.getDefault().tagAsGlobalSummary(meter, "Core UI Startup w/Jobs", Dimension.ELAPSED_PROCESS);
			meter.commit();
			Performance.getDefault().assertPerformanceInRelativeBand(meter, Dimension.ELAPSED_PROCESS, -50, 5);
		}
		finally
		{
			meter.dispose();
		}
	}
}