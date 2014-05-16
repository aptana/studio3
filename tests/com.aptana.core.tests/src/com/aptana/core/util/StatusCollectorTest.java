/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.core.CorePlugin;

/**
 * {@link StatusCollector} tests.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class StatusCollectorTest
{
	private static final int OLD_STATUS = 0;
	private static final int NEW_STATUS = 1;

	private StatusCollector statusCollector;
	private IStatus[] notification;
	private IStatusCollectorListener listener = new IStatusCollectorListener()
	{

		public void statusChanged(IStatus oldStatus, IStatus newStatus)
		{
			notification[OLD_STATUS] = oldStatus;
			notification[NEW_STATUS] = newStatus;
		}
	};

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
//	@Override
	@Before
	public void setUp() throws Exception
	{
		statusCollector = new StatusCollector();
		notification = new IStatus[2];
		statusCollector.addListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
//	@Override
	@After
	public void tearDown() throws Exception
	{
		statusCollector = null;
		notification = null;
//		super.tearDown();
	}

	@Test
	public void testAddRemoveStatus() throws Exception
	{
		Status status1 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error1");
		Status status2 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error2");
		statusCollector.setStatus(status1, "abc");
		statusCollector.setStatus(status2, "def");

		assertEquals("Expected existing status", status1, statusCollector.getStatus("abc"));
		assertEquals("Expected existing status", status2, statusCollector.getStatus("def"));

		statusCollector.clearStatus("abc");
		assertNull("Expected null status", statusCollector.getStatus("abc"));
		assertEquals("Expected existing status", status2, statusCollector.getStatus("def"));

		statusCollector.setStatus(null, "def");
		assertNull("Expected null status", statusCollector.getStatus("abc"));
		assertNull("Expected null status", statusCollector.getStatus("def"));
	}

	@Test
	public void testStatusAddNotifications() throws Exception
	{
		Status status1 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error1");
		Status status2 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error2");

		statusCollector.setStatus(status1, "abc");
		assertEquals("Expected a newStatus in the notification", status1, notification[NEW_STATUS]);
		assertNull("Expected null oldStatus in the notification", notification[OLD_STATUS]);

		statusCollector.setStatus(status2, "def");
		assertEquals("Expected a newStatus in the notification", status2, notification[NEW_STATUS]);
		assertNull("Expected null oldStatus in the notification", notification[OLD_STATUS]);

	}

	@Test
	public void testStatusAddSameNotification() throws Exception
	{
		Status status1 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error1");

		// Add this status for the first time.
		statusCollector.setStatus(status1, "abc");
		assertEquals("Expected a newStatus in the notification", status1, notification[NEW_STATUS]);
		assertNull("Expected null oldStatus in the notification", notification[OLD_STATUS]);

		// Add the same status again, with the same key.
		// Clear the notification array before the next test. We check that it was not effected (no notification was
		// fired).
		notification = new IStatus[2];
		statusCollector.setStatus(status1, "abc");
		assertNull("Expected null oldStatus in the notification", notification[NEW_STATUS]);
		assertNull("Expected null oldStatus in the notification", notification[OLD_STATUS]);
	}

	@Test
	public void testStatusOverideNotification() throws Exception
	{
		Status status1 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error1");
		Status status2 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error2");

		statusCollector.setStatus(status1, "abc");
		statusCollector.setStatus(status2, "abc");

		assertEquals("Expected a newStatus in the notification", status2, notification[NEW_STATUS]);
		assertEquals("Expected an oldStatus in the notification", status1, notification[OLD_STATUS]);
	}

	@Test
	public void testStatusRemoveNotification() throws Exception
	{
		Status status1 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error1");

		statusCollector.setStatus(status1, "abc");
		statusCollector.clearStatus("abc");

		assertNull("Expected null newStatus in the notification", notification[NEW_STATUS]);
		assertEquals("Expected an oldStatus in the notification", status1, notification[OLD_STATUS]);

		Status status2 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error2");
		statusCollector.setStatus(status2, "def");
		statusCollector.setStatus(null, "def");
		assertNull("Expected null newStatus in the notification", notification[NEW_STATUS]);
		assertEquals("Expected an oldStatus in the notification", status2, notification[OLD_STATUS]);
	}

	@Test
	public void testGetStatuses() throws Exception
	{
		Status status1 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error1");
		Status status2 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error2");
		Status status3 = new Status(IStatus.WARNING, CorePlugin.PLUGIN_ID, "Warning");
		Status status4 = new Status(IStatus.INFO, CorePlugin.PLUGIN_ID, "Info");
		statusCollector.setStatus(status1, "abc");
		statusCollector.setStatus(status2, "def");
		statusCollector.setStatus(status3, "aaa");
		statusCollector.setStatus(status4, "bbb");
		statusCollector.setStatus(Status.OK_STATUS, "ccc");
		statusCollector.setStatus(Status.CANCEL_STATUS, "ddd");

		IStatus[] statuses = statusCollector.getStatuses(IStatus.ERROR);
		assertNotNull("Unexpected null result", statuses);
		assertEquals("Unexpected statuses count", 2, statuses.length);
		assertNotNull("Unexpected null status", statuses[0]);
		assertNotNull("Unexpected null status", statuses[1]);

		List<IStatus> statusList = Arrays.asList(statuses);
		assertTrue("Expected a status", statusList.contains(status1));
		assertTrue("Expected a status", statusList.contains(status2));

		statuses = statusCollector.getStatuses(IStatus.WARNING);
		assertNotNull("Unexpected null result", statuses);
		assertEquals("Unexpected statuses count", 1, statuses.length);
		assertEquals("Unexpected statuse", status3, statuses[0]);
	}

	@Test
	public void testGetMixedStatuses() throws Exception
	{
		Status status1 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error1");
		Status status2 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error2");
		Status status3 = new Status(IStatus.WARNING, CorePlugin.PLUGIN_ID, "Warning");
		Status status4 = new Status(IStatus.INFO, CorePlugin.PLUGIN_ID, "Info");
		statusCollector.setStatus(status1, "abc");
		statusCollector.setStatus(status2, "def");
		statusCollector.setStatus(status3, "aaa");
		statusCollector.setStatus(status4, "bbb");
		statusCollector.setStatus(Status.OK_STATUS, "ccc");
		statusCollector.setStatus(Status.CANCEL_STATUS, "ddd");

		IStatus[] statuses = statusCollector.getStatuses(IStatus.ERROR | IStatus.INFO);
		assertNotNull("Unexpected null result", statuses);
		assertEquals("Unexpected statuses count", 3, statuses.length);
		List<IStatus> statusList = Arrays.asList(statuses);
		assertTrue("Expected a status", statusList.contains(status1));
		assertTrue("Expected a status", statusList.contains(status2));
		assertTrue("Expected a status", statusList.contains(status4));

		statuses = statusCollector.getStatuses(IStatus.ERROR | IStatus.INFO | IStatus.WARNING);
		assertNotNull("Unexpected null result", statuses);
		assertEquals("Unexpected statuses count", 4, statuses.length);
		statusList = Arrays.asList(statuses);
		assertTrue("Expected a status", statusList.contains(status1));
		assertTrue("Expected a status", statusList.contains(status2));
		assertTrue("Expected a status", statusList.contains(status3));
		assertTrue("Expected a status", statusList.contains(status4));
	}

	@Test
	public void testGetStatusCount() throws Exception
	{
		Status status1 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error1");
		Status status2 = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, "Error2");
		Status status3 = new Status(IStatus.WARNING, CorePlugin.PLUGIN_ID, "Warning");
		Status status4 = new Status(IStatus.INFO, CorePlugin.PLUGIN_ID, "Info");
		statusCollector.setStatus(status1, "abc");
		statusCollector.setStatus(status2, "def");
		statusCollector.setStatus(status3, "aaa");
		statusCollector.setStatus(status4, "bbb");
		statusCollector.setStatus(Status.OK_STATUS, "ccc");
		statusCollector.setStatus(Status.CANCEL_STATUS, "ddd");

		assertEquals("Unexpected status count", 1, statusCollector.getStatusCount(IStatus.INFO));
		assertEquals("Unexpected status count", 2, statusCollector.getStatusCount(IStatus.ERROR));
		assertEquals("Unexpected status count", 5,
				statusCollector.getStatusCount(IStatus.ERROR | IStatus.INFO | IStatus.WARNING | IStatus.CANCEL));
	}
}
