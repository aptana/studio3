/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.internal.views;

import org.eclipse.osgi.util.NLS;

/**
 * @author Pavel Petrochenko
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.webserver.ui.internal.views.messages"; //$NON-NLS-1$

	public static String ServersView_NAME;
	public static String ServersView_STATUS;
	public static String ServersView_STATUS_STARTED;
	public static String ServersView_STATUS_STARTING;
	public static String ServersView_STATUS_STOPPING;
	public static String ServersView_STATUS_STOPPED;
	public static String ServersView_STATUS_UNKNOWN;
	public static String ServersView_STATUS_NOT_APPLICABLE;
	public static String ServersView_DESCRIPTION;
	public static String ServersView_TYPE;
	public static String GenericServersView_HOST;
	public static String GenericServersView_PORT;

	private Messages()
	{
	}

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
