/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import junit.framework.TestCase;

/**
 * @author Max Stepanov
 */
public class SocketUtilTest extends TestCase {

	public void testFindFreePort() {
		assertTrue(SocketUtil.findFreePort(null) > 0);
	}

	public void testFindFreePortInRange() {
		int port = SocketUtil.findFreePort(null, 8000, 8010);
		assertTrue(port >= 8000 && port <= 8010);
	}

	public void testFindFreePortInInvalidRange() {
		int port = SocketUtil.findFreePort(null, 1, 10);
		assertEquals(-1, port);
	}

	public void testFindFreePortInTakenRange() throws IOException {
		ServerSocket socket = new ServerSocket(9000);
		int port = SocketUtil.findFreePort(null, 9000, 9000);
		socket.close();
		assertEquals(-1, port);
	}

	public void testGetLocalAddresses() {
		InetAddress[] addresses = SocketUtil.getLocalAddresses();
		assertTrue(addresses.length > 0);
		boolean passed = false;
		for (InetAddress i : addresses) {
			if (passed = "127.0.0.1".equals(i.getHostAddress())) {
				break;
			}
		}
		assertTrue(passed);
	}

	public void testGetNonLoopbackLocalAdresses() {
		InetAddress[] addresses = SocketUtil.getNonLoopbackLocalAdresses();
		assertTrue(addresses.length > 0);
		for (InetAddress i : addresses) {
			assertEquals(false, i.isLoopbackAddress());
			assertNotSame("127.0.0.1", i.getHostAddress());
		}
	}

}
