package com.aptana.editor.common.contentassist;

import com.aptana.ui.epl.UIEplPlugin;

/**
 * A interface to capture the various scopes available during debugging. These need to match the items in the .options
 * file at the root of the plugin
 * 
 * @author Ingo Muschenetz
 */
public interface IUiEplScopes
{
	/**
	 * Items related to computing relevance of elements
	 */
	String RELEVANCE = UIEplPlugin.PLUGIN_ID + "/debug/relevance"; //$NON-NLS-1$
}