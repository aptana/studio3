package com.aptana.portal.ui;

/**
 * A interface to capture the various scopes available during debugging. These need to match the items in the .options
 * file at the root of the plugin
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public interface IDebugScopes
{
	/**
	 * Items related to the Studio's start page
	 */
	String START_PAGE = PortalUIPlugin.PLUGIN_ID + "/debug/startpage"; //$NON-NLS-1$

}