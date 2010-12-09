/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
	/**
	 * SocketUtil
	 */
	private SocketUtil() {
	}

	/**
	 * findFreePort
	 * 
	 * @return int
	 */
	public static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
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
}
