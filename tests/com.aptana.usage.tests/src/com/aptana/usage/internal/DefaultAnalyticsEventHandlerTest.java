package com.aptana.usage.internal;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

import com.aptana.usage.IUsageSystemProperties;

public class DefaultAnalyticsEventHandlerTest
{

	@After
	public void tearDown() throws Exception
	{
		System.clearProperty(IUsageSystemProperties.ANALYTICS_URL);
	}

	@Test
	public void testGetDefaultAnalyticsURL()
	{
		DefaultAnalyticsEventHandler handler = new DefaultAnalyticsEventHandler();
		assertEquals(DefaultAnalyticsEventHandler.DEFAULT_URL, handler.getAnalyticsURL());
	}

	@Test
	public void testAnalyticsURLCanBeOverridenBySystemProperty()
	{
		final String newURL = "http://example.com";
		System.setProperty(IUsageSystemProperties.ANALYTICS_URL, newURL);
		DefaultAnalyticsEventHandler handler = new DefaultAnalyticsEventHandler();
		assertEquals(newURL, handler.getAnalyticsURL());
	}

	@Test
	public void testGetDefaultTimeout()
	{
		DefaultAnalyticsEventHandler handler = new DefaultAnalyticsEventHandler();
		assertEquals(DefaultAnalyticsEventHandler.DEFAULT_TIMEOUT, handler.getTimeout());
	}
}
