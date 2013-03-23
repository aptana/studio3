/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui;

/**
 * Holds preferences keys that are used by the portal (dev-toolbox)
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public interface IPortalPreferences
{
	/**
	 * Holds the preferences key for the installed applications cached versions and location.<br>
	 * The value that is stored on that key is a JSON string in that structure: {appName:{version:appVersion,
	 * location:appLocation}}
	 */
	String CACHED_VERSIONS_PROPERTY_NAME = PortalUIPlugin.PLUGIN_ID + ".cached_versions"; //$NON-NLS-1$
	String CACHED_VERSION_PROPERTY = "version"; //$NON-NLS-1$
	String CACHED_LOCATION_PROPERTY = "location"; //$NON-NLS-1$

	/**
	 * indicate whether or not the developer toolbox should be displayed on startup
	 */
	String SHOULD_OPEN_DEV_TOOLBOX = "open_developer_toolbox"; //$NON-NLS-1$

	/**
	 * Holds the last Studio version that existed when the start page was opened
	 */
	String LAST_KNOWN_STUDIO_VERSION = "last_known_studio_version"; //$NON-NLS-1$

	/**
	 * The preference key used to hold the recently created project.
	 */
	String RECENTLY_CREATED_PROJECT = "recently_created_project"; //$NON-NLS-1$

}
