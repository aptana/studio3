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

package com.aptana.webserver.core.preferences;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.util.SocketUtil;
import com.aptana.webserver.core.WebServerCorePlugin;

/**
 * @author Max Stepanov
 *
 */
public class WebServerPreferences {

	private static final String PORTS_PATTERN = "^(\\d+)(-(\\d+))?$"; //$NON-NLS-1$
	
	private WebServerPreferences() {
	}

	/**
	 * Returns preferences-specified local webserver address
	 * 
	 * @return
	 */
	public static InetAddress getServerAddress() {
		IEclipsePreferences node = new DefaultScope().getNode(WebServerCorePlugin.PLUGIN_ID);
		String address = node.get(IWebServerPreferenceConstants.PREF_HTTP_SERVER_PORTS, null);
		for(InetAddress i : SocketUtil.getLocalAddresses()) {
			if(i.getHostAddress().equals(address)) {
				return i;
			}
		}
		try {
			return InetAddress.getByName(IWebServerPreferenceConstants.DEFAULT_HTTP_SERVER_ADDRESS);
		} catch (UnknownHostException e) {
			return null;
		}
	}
	
	/**
	 * Returns preferences-specified local webserver port range
	 * @return
	 */
	public static int[] getPortRange() {
		IEclipsePreferences node = new DefaultScope().getNode(WebServerCorePlugin.PLUGIN_ID);
		int portsStart = IWebServerPreferenceConstants.DEFAULT_HTTP_SERVER_PORTS_RANGE[0];
		int portsEnd = IWebServerPreferenceConstants.DEFAULT_HTTP_SERVER_PORTS_RANGE[1];
		String portsString = node.get(IWebServerPreferenceConstants.PREF_HTTP_SERVER_PORTS, null);
		if (portsString != null && portsString.length() > 0) {
			Matcher matcher = Pattern.compile(PORTS_PATTERN).matcher(portsString); 
			if (matcher.matches()) {
				try {
					int start = Integer.parseInt(matcher.group(1));
					int end = start;
					if ( matcher.group(2) != null ) {
						end = Integer.parseInt(matcher.group(3));
					}
					if ( start < end ) {
						portsStart = start;
						portsEnd = end;
					}
				} catch (NumberFormatException e) {
				}				
			}
		}
		return new int[] { portsStart, portsEnd };
	}

}
