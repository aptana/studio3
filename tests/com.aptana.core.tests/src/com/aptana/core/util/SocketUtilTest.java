/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.junit.Test;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("nls")
public class SocketUtilTest
{

	@Test
	public void testFindFreePort()
	{
		int port = SocketUtil.findFreePort(null);
		assertTrue("Failed to find any free port", port > 0);
		try
		{
			new ServerSocket(port).close();
		}
		catch (IOException e)
		{
			fail("Found port is not free");
		}
	}

	@Test
	public void testFindFreePortInRange()
	{
		int port = SocketUtil.findFreePort(null, 8000, 8010);
		assertTrue("Failed to find free port in range", port >= 8000 && port <= 8010);
		try
		{
			new ServerSocket(port).close();
		}
		catch (IOException e)
		{
			fail("Found port is not free");
		}
	}

	@Test
	public void testFindFreePortInInvalidRange()
	{
		int port = SocketUtil.findFreePort(null, 1, 10);
		assertEquals("Found free port in invalid range", -1, port);
	}

	@Test
	public void testFindFreePortInTakenRange() throws IOException
	{
		ServerSocket socket = new ServerSocket(9000);
		int port = SocketUtil.findFreePort(null, 9000, 9000);
		socket.close();
		assertEquals("Found free port in taken range", -1, port);
	}

	@Test
	public void testGetLocalAddresses()
	{
		InetAddress[] addresses = SocketUtil.getLocalAddresses();
		assertTrue(addresses.length > 0);
		boolean passed = false;
		for (InetAddress i : addresses)
		{
			if (passed = "127.0.0.1".equals(i.getHostAddress()))
			{
				break;
			}
		}
		assertTrue("Finding loopback 127.0.0.1 address failed", passed);
	}

	@Test
	public void testGetNonLoopbackLocalAdresses()
	{
		InetAddress[] addresses = SocketUtil.getNonLoopbackLocalAdresses();
		assertTrue(addresses.length > 0);
		for (InetAddress i : addresses)
		{
			assertEquals("Loopback address returned when non-loopback expected", false, i.isLoopbackAddress());
			assertFalse("Loopback 127.0.0.1 address returned when non-loopback expected",
					"127.0.0.1".equals(i.getHostAddress()));
		}
	}

	@Test
	public void testIPv4() throws Exception
	{
		assertTrue("Expected a valid IPv4 address for '127.0.0.1'", SocketUtil.isValidIPv4("127.0.0.1"));
		assertFalse("Expected an invalid IPv4 address for '127.0.0.a'", SocketUtil.isValidIPv4("127.0.0.a"));
		assertFalse("Expected an invalid IPv4 address for '127.0.0.256'", SocketUtil.isValidIPv4("127.0.0.256"));
		assertFalse("Expected an invalid IPv4 address for '127.0.0'", SocketUtil.isValidIPv4("127.0.0"));
	}
}
