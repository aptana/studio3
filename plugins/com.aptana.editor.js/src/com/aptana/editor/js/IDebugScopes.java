package com.aptana.editor.js;

/**
 * A interface to capture the various scopes available during debugging. These need to match the items in the .options
 * file at the root of the plugin
 * 
 * @author Ingo Muschenetz
 */
public interface IDebugScopes
{
	/**
	 * Items related to the content assist process
	 */
	String CONTENT_ASSIST_TYPES = JSPlugin.PLUGIN_ID + "/debug/show_content_assist_types"; //$NON-NLS-1$

}
