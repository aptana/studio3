/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.xhr;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ingo Muschenetz
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.aptana.js.debug.ui.internal.xhr.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * AJAXMonitorPage_Request
	 */
	public static String AJAXMonitorPage_Request;

	/**
	 * AJAXMonitorPage_Response
	 */
	public static String AJAXMonitorPage_Response;

	/**
	 * AJAXMonitorPage_URL
	 */
	public static String AJAXMonitorPage_URL;

	/**
	 * AJAXMonitorPage_Method
	 */
	public static String AJAXMonitorPage_Method;

	/**
	 * AJAXMonitorPage_Sent
	 */
	public static String AJAXMonitorPage_Sent;

	/**
	 * AJAXMonitorPage_Received
	 */
	public static String AJAXMonitorPage_Received;

	/**
	 * AJAXMonitorPage_Name
	 */
	public static String AJAXMonitorPage_Name;

	/**
	 * AJAXMonitorPage_Value
	 */
	public static String AJAXMonitorPage_Value;

	/**
	 * AJAXMonitorPage_Headers
	 */
	public static String AJAXMonitorPage_Headers;

	/**
	 * AJAXMonitorPage_Body
	 */
	public static String AJAXMonitorPage_Body;
}
