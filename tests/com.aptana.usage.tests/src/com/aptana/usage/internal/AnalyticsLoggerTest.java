package com.aptana.usage.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.FileUtil;
import com.aptana.usage.AnalyticsEvent;

public class AnalyticsLoggerTest
{

	private AnalyticsLogger logger;
	private IPath dir;

	@Before
	public void setUp() throws Exception
	{
		dir = FileUtil.getTempDirectory().append("events_" + System.currentTimeMillis());
		logger = new AnalyticsLogger(dir);
	}

	@After
	public void tearDown() throws Exception
	{
		FileUtil.deleteRecursively(dir.toFile());
		dir = null;
		logger = null;
	}

	@Test
	public void testLogEvent()
	{
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("key1", "value1");
		AnalyticsEvent event = new AnalyticsEvent("testing", "testLogEvent", payload);
		logger.logEvent(event);
		File file = dir.append(event.hashCode() + ".json").toFile();
		assertTrue(file.exists());
		assertTrue(file.isFile());

		List<AnalyticsEvent> events = logger.getEvents();
		assertEquals(1, events.size());
		assertEquals(event, events.get(0));

		logger.clearEvent(event);
		assertFalse(file.exists());
		assertFalse(file.isFile());

		events = logger.getEvents();
		assertEquals(0, events.size());
	}

	@Test
	public void testClearEvents()
	{
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("key1", "value1");
		AnalyticsEvent event1 = new AnalyticsEvent("testing", "testClearEvents", payload);
		logger.logEvent(event1);
		File eventFile1 = dir.append(event1.hashCode() + ".json").toFile();
		assertTrue(eventFile1.exists());
		assertTrue(eventFile1.isFile());

		Map<String, String> payload2 = new HashMap<String, String>();
		payload.put("key2", "value2");
		AnalyticsEvent event2 = new AnalyticsEvent("testing", "testClearEvents2", payload2);
		logger.logEvent(event2);
		File eventFile2 = dir.append(event2.hashCode() + ".json").toFile();
		assertTrue(eventFile2.exists());
		assertTrue(eventFile2.isFile());

		List<AnalyticsEvent> events = logger.getEvents();
		assertEquals(2, events.size());
		assertTrue(events.contains(event1));
		assertTrue(events.contains(event2));

		logger.clearEvents();
		assertFalse(eventFile1.exists());
		assertFalse(eventFile1.isFile());
		assertFalse(eventFile2.exists());
		assertFalse(eventFile2.isFile());

		events = logger.getEvents();
		assertEquals(0, events.size());
	}
}
