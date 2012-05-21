/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.http;

/**
 * Debug scopes used for logging errors, warnings and info into the Studio's log file.
 */
public interface IDebugScopes
{
	/**
	 * Generic debug scope
	 */
	String DEBUG = HttpFilesystemPlugin.PLUGIN_ID + "/debug"; //$NON-NLS-1$

	/**
	 * Debugging API requests and responses
	 */
	String FILESYSTEM = HttpFilesystemPlugin.PLUGIN_ID + "/debug/filesystem"; //$NON-NLS-1$
}
