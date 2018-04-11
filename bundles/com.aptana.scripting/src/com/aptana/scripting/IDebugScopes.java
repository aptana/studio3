package com.aptana.scripting;

/**
 * A interface to capture the various scopes available during debugging. These need to match the items in the .options
 * file at the root of the plugin
 * 
 * @author Ingo Muschenetz
 */
public interface IDebugScopes
{
	/**
	 * trace bundle file event events
	 */
	String SHOW_BUNDLE_MONITOR_FILE_EVENTS = ScriptingActivator.PLUGIN_ID + "/debug/show_bundle_monitor_file_events"; //$NON-NLS-1$

	/**
	 * Trace bundle resource events
	 */
	String SHOW_BUNDLE_MONITOR_RESOURCE_EVENTS = ScriptingActivator.PLUGIN_ID
			+ "/debug/show_bundle_monitor_resource_events"; //$NON-NLS-1$

	/**
	 * Element registration
	 */
	String SHOW_ELEMENT_REGISTRATION = ScriptingActivator.PLUGIN_ID + "/debug/show_element_registration"; //$NON-NLS-1$

	/**
	 * Load information
	 */
	String SHOW_BUNDLE_LOAD_INFO = ScriptingActivator.PLUGIN_ID + "/debug/show_bundle_load_info"; //$NON-NLS-1$

}
