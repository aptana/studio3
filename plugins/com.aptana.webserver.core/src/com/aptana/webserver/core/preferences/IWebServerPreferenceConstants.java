/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.webserver.core.preferences;

import com.aptana.webserver.core.WebServerCorePlugin;

/**
 * @author Max Stepanov
 *
 */
public interface IWebServerPreferenceConstants {

	String PREFIX = WebServerCorePlugin.PLUGIN_ID;
	
	/**
	 * IP address used by built-in HTTP server
	 */
	String PREF_HTTP_SERVER_ADDRESS = PREFIX + ".http_server_address"; //$NON-NLS-1$

	/**
	 * Ports list used by built-in HTTP server
	 */
	String PREF_HTTP_SERVER_PORTS = PREFIX + ".http_server_ports"; //$NON-NLS-1$

	/**
	 * Default IP address
	 */
	String DEFAULT_HTTP_SERVER_ADDRESS = "127.0.0.1"; //$NON-NLS-1$
	
	/**
	 * Default ports range
	 */
	int[] DEFAULT_HTTP_SERVER_PORTS_RANGE = new int[] { 8020, 8079 };
}
