/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Max Stepanov
 */
public final class SocketUtil {

	private SocketUtil() {
	}

	/**
	 * Find free port for the specified IP address
	 * 
	 * @return int
	 */
	public static int findFreePort(InetAddress address) {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0, 0, address);
			socket.setReuseAddress(true);
			return socket.getLocalPort();
		} catch (IOException ignore) {
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException ignore) {
				}
			}
		}
		return -1;
	}

	/**
	 * Find free port in range for the specified IP address
	 * 
	 * @return int
	 */
	public static int findFreePort(InetAddress address, int start, int end) {
		ServerSocket socket = null;
		try {
			for (int port = start; port <= end; ++port) {
				try {
					socket = new ServerSocket(port, 0, address);
					socket.setReuseAddress(true);
					return socket.getLocalPort();
				} catch (IOException ignore) {
				}
			}
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException ignore) {
				}
			}
		}
		return -1;
	}

	/**
	 * Returns list of machine assigned IP addresses
	 * @return
	 */
	public static InetAddress[] getLocalAddresses() {
		List<InetAddress> addrs = new ArrayList<InetAddress>();
		try {
			for (Enumeration<NetworkInterface> e1 = NetworkInterface.getNetworkInterfaces(); e1.hasMoreElements();) {
				NetworkInterface iface = e1.nextElement();
				for (Enumeration<InetAddress> e2 = iface.getInetAddresses(); e2.hasMoreElements();) {
					InetAddress inetAddr = e2.nextElement();
					if (inetAddr instanceof Inet4Address) {
						if (!addrs.contains(inetAddr)) {
							addrs.add(inetAddr);
						}
					}
				}
			}
		} catch (SocketException ignore) {
		}
		return addrs.toArray(new InetAddress[addrs.size()]);
	}
	
	/**
	 * Returns list of machine assigned IP addresses excluding localhost/127.0.0.1
	 * @return
	 */
	public static InetAddress[] getNonLoopbackLocalAdresses() {
		List<InetAddress> addrs = new ArrayList<InetAddress>();
		for (InetAddress inetAddr : getLocalAddresses()) {
			if (!inetAddr.isLoopbackAddress()) {
				if (isPublic(inetAddr)) {
					addrs.add(0, inetAddr);
				} else {
					addrs.add(inetAddr);
				}
			}
		}
		return addrs.toArray(new InetAddress[addrs.size()]);
	}
	
	private static boolean isPublic(InetAddress inetAddress) {
		String hostAddress = inetAddress.getHostAddress();
		if (hostAddress.startsWith("10.") //$NON-NLS-1$
				|| hostAddress.startsWith("192.168.") //$NON-NLS-1$
				|| hostAddress.startsWith("169.254.") //$NON-NLS-1$
				|| hostAddress.startsWith("127.")) { //$NON-NLS-1$
			return false;
		}
		if (hostAddress.startsWith("172.")) { //$NON-NLS-1$
			return !(inetAddress.getAddress()[1] >= 16 && inetAddress.getAddress()[1] <= 31);
		}
		return true;
	}
}
