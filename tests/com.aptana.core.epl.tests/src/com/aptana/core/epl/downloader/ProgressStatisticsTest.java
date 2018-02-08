package com.aptana.core.epl.downloader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProgressStatisticsTest
{

	private ProgressStatistics stats;
	private final long defaultReportInterval = 1000;

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
		stats = null;
	}

	@Test
	public void test() throws InterruptedException
	{
		long total = 100000;
		// TODO Modify so we can pass in the minimum time before values get reported, defaults to 1 second right now...
		stats = new ProgressStatistics(URI.create("http://www.example.com"), "fileName", total);
		assertTrue(stats.shouldReport()); // first call should be true
		assertFalse(stats.shouldReport()); // then should be false until report interval has elapsed
		// methods return 0 until duration is at least 1 second
		assertEquals(0, stats.getRecentSpeed());
		assertEquals(0.0, stats.getPercentage(), 0.01);
		assertEquals(0, stats.getAverageSpeed());
		assertEquals(defaultReportInterval, stats.getReportInterval()); // default value
		stats.increase(10000);
		while (stats.getDuration() < 1000)
		{
			Thread.sleep(50);
		}
		stats.increase(10000);
		assertTrue(stats.getRecentSpeed() > 0);
		assertTrue(stats.getAverageSpeed() > 0);
		assertTrue(stats.shouldReport());
		// TODO Test #report()
	}

}
