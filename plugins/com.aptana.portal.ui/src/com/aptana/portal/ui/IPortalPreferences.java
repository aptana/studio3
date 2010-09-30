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
	 * The value that is stored on that key is a JSON string in that structure: {appName:{version:appVersion, location:appLocation}}
	 */
	String CACHED_VERSIONS_PROPERTY_NAME = PortalUIPlugin.PLUGIN_ID + ".cached_versions"; //$NON-NLS-1$
	String CACHED_VERSION_PROPERTY = "version"; //$NON-NLS-1$
	String CACHED_LOCATION_PROPERTY = "location"; //$NON-NLS-1$
	
	/**
	 * indicate whether or not the developer toolbox should be displayed on startup
	 */
	String SHOULD_OPEN_DEV_TOOLBOX = "open_developer_toolbox"; //$NON-NLS-1$
}
